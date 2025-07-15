package io.devexpert.playstore

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.AndroidPublisherScopes
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream

/**
 * Client for interacting with Google Play Console API
 */
class PlayStoreClient(
    private val serviceAccountKeyPath: String,
    private val applicationName: String = "Play Store MCP Server"
) {
    private val logger = LoggerFactory.getLogger(PlayStoreClient::class.java)
    
    private val publisher: AndroidPublisher by lazy {
        initializePublisher()
    }
    
    private fun initializePublisher(): AndroidPublisher {
        logger.info("Initializing Google Play Console API client...")
        
        try {
            // Initialize HTTP transport and JSON factory
            val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
            val jsonFactory = GsonFactory.getDefaultInstance()
            
            // Load service account credentials
            val credentialsFile = File(serviceAccountKeyPath)
            if (!credentialsFile.exists()) {
                throw IllegalArgumentException("Service account key file not found: $serviceAccountKeyPath")
            }
            
            val credential = GoogleCredential.fromStream(
                FileInputStream(credentialsFile),
                httpTransport,
                jsonFactory
            ).createScoped(listOf(AndroidPublisherScopes.ANDROIDPUBLISHER))
            
            // Build the API client
            val publisher = AndroidPublisher.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(applicationName)
                .build()
            
            logger.info("Google Play Console API client initialized successfully")
            return publisher
            
        } catch (e: Exception) {
            logger.error("Failed to initialize Google Play Console API client", e)
            throw PlayStoreException("Failed to initialize API client: ${e.message}", e)
        }
    }
    
    /**
     * Get list of applications for the developer account
     */
    suspend fun getApplications(): List<PlayStoreApp> {
        logger.debug("Fetching applications from Play Store...")
        
        return try {
            // Note: The Google Play Console API doesn't have a direct "list apps" endpoint
            // This would typically be implemented by maintaining a list of package names
            // or using other Google APIs. For now, we'll return a placeholder.
            
            logger.warn("Direct app listing not available - using configured package names")
            emptyList()
            
        } catch (e: Exception) {
            logger.error("Failed to fetch applications", e)
            throw PlayStoreException("Failed to fetch applications: ${e.message}", e)
        }
    }
    
    /**
     * Get app details for a specific package
     */
    suspend fun getAppDetails(packageName: String): PlayStoreApp? {
        logger.debug("Fetching app details for: $packageName")
        
        return try {
            val editRequest = publisher.edits().insert(packageName, null)
            val edit = editRequest.execute()
            val editId = edit.id
            
            // Get app details
            val appDetails = publisher.edits().details().get(packageName, editId).execute()
            val listings = publisher.edits().listings().list(packageName, editId).execute()
            
            // Get track information
            val tracks = publisher.edits().tracks().list(packageName, editId).execute()
            
            // Clean up the edit
            publisher.edits().delete(packageName, editId).execute()
            
            PlayStoreApp(
                packageName = packageName,
                name = appDetails?.defaultLanguage?.let { defaultLang ->
                    listings?.listings?.find { it.language == defaultLang }?.title
                } ?: packageName,
                status = determineAppStatus(tracks?.tracks),
                currentVersionCode = getCurrentVersionCode(tracks?.tracks),
                currentVersionName = getCurrentVersionName(tracks?.tracks),
                lastUpdated = System.currentTimeMillis()
            )
            
        } catch (e: Exception) {
            logger.error("Failed to fetch app details for $packageName", e)
            null
        }
    }
    
    /**
     * Get release information for an app
     */
    suspend fun getReleases(packageName: String): List<PlayStoreRelease> {
        logger.debug("Fetching releases for: $packageName")
        
        return try {
            val editRequest = publisher.edits().insert(packageName, null)
            val edit = editRequest.execute()
            val editId = edit.id
            
            val tracks = publisher.edits().tracks().list(packageName, editId).execute()
            val releases = mutableListOf<PlayStoreRelease>()
            
            tracks?.tracks?.forEach { track ->
                track.releases?.forEach { release ->
                    releases.add(
                        PlayStoreRelease(
                            packageName = packageName,
                            track = track.track ?: "unknown",
                            status = release.status ?: "unknown",
                            versionCode = release.versionCodes?.firstOrNull()?.toLong() ?: 0,
                            rolloutPercentage = release.userFraction?.times(100)?.toInt() ?: 100,
                            startTime = System.currentTimeMillis(),
                            completedTime = if (release.status == "completed") System.currentTimeMillis() else null
                        )
                    )
                }
            }
            
            // Clean up the edit
            publisher.edits().delete(packageName, editId).execute()
            
            releases
            
        } catch (e: Exception) {
            logger.error("Failed to fetch releases for $packageName", e)
            throw PlayStoreException("Failed to fetch releases: ${e.message}", e)
        }
    }
    
    /**
     * Deploy a new version of an app
     */
    suspend fun deployApp(
        packageName: String,
        track: String,
        apkPath: String,
        versionCode: Long,
        releaseNotes: String?
    ): PlayStoreDeploymentResult {
        logger.info("Deploying app: $packageName to $track track, version $versionCode")
        
        return try {
            // Create a new edit
            val editRequest = publisher.edits().insert(packageName, null)
            val edit = editRequest.execute()
            val editId = edit.id
            
            logger.debug("Created edit: $editId")
            
            // Upload the APK/AAB
            val apkFile = File(apkPath)
            if (!apkFile.exists()) {
                throw PlayStoreException("APK/AAB file not found: $apkPath")
            }
            
            val uploadRequest = if (apkPath.endsWith(".aab")) {
                publisher.edits().bundles().upload(packageName, editId, 
                    com.google.api.client.http.FileContent("application/octet-stream", apkFile))
            } else {
                publisher.edits().apks().upload(packageName, editId,
                    com.google.api.client.http.FileContent("application/vnd.android.package-archive", apkFile))
            }
            
            uploadRequest.execute()
            logger.debug("Upload completed, version code: $versionCode")
            
            // Create a release
            val release = com.google.api.services.androidpublisher.model.TrackRelease()
            release.name = "Release $versionCode"
            release.versionCodes = listOf(versionCode)
            release.status = "completed"
            
            if (!releaseNotes.isNullOrBlank()) {
                val releaseNote = com.google.api.services.androidpublisher.model.LocalizedText()
                releaseNote.language = "en-US"
                releaseNote.text = releaseNotes
                release.releaseNotes = listOf(releaseNote)
            }
            
            // Update the track
            val trackUpdate = com.google.api.services.androidpublisher.model.Track()
            trackUpdate.track = track
            trackUpdate.releases = listOf(release)
            
            publisher.edits().tracks().update(packageName, editId, track, trackUpdate).execute()
            
            // Commit the edit
            val commitRequest = publisher.edits().commit(packageName, editId)
            commitRequest.execute()
            
            logger.info("Successfully deployed $packageName version $versionCode to $track")
            
            PlayStoreDeploymentResult(
                success = true,
                deploymentId = editId,
                packageName = packageName,
                track = track,
                versionCode = versionCode,
                message = "Successfully deployed to $track track"
            )
            
        } catch (e: Exception) {
            logger.error("Failed to deploy app $packageName", e)
            PlayStoreDeploymentResult(
                success = false,
                deploymentId = null,
                packageName = packageName,
                track = track,
                versionCode = versionCode,
                message = "Deployment failed: ${e.message}",
                error = e
            )
        }
    }
    
    /**
     * Promote a release from one track to another
     */
    suspend fun promoteRelease(
        packageName: String,
        fromTrack: String,
        toTrack: String,
        versionCode: Long
    ): PlayStoreDeploymentResult {
        logger.info("Promoting $packageName version $versionCode from $fromTrack to $toTrack")
        
        return try {
            val editRequest = publisher.edits().insert(packageName, null)
            val edit = editRequest.execute()
            val editId = edit.id
            
            // Get the release from source track
            val sourceTrackData = publisher.edits().tracks().get(packageName, editId, fromTrack).execute()
            val sourceRelease = sourceTrackData.releases?.find { release ->
                release.versionCodes?.contains(versionCode) == true
            } ?: throw PlayStoreException("Version $versionCode not found in $fromTrack track")
            
            // Create new release for target track
            val newRelease = com.google.api.services.androidpublisher.model.TrackRelease()
            newRelease.name = sourceRelease.name
            newRelease.versionCodes = sourceRelease.versionCodes
            newRelease.status = "completed"
            newRelease.releaseNotes = sourceRelease.releaseNotes
            
            val targetTrack = com.google.api.services.androidpublisher.model.Track()
            targetTrack.track = toTrack
            targetTrack.releases = listOf(newRelease)
            
            publisher.edits().tracks().update(packageName, editId, toTrack, targetTrack).execute()
            
            // Commit the changes
            publisher.edits().commit(packageName, editId).execute()
            
            logger.info("Successfully promoted $packageName version $versionCode to $toTrack")
            
            PlayStoreDeploymentResult(
                success = true,
                deploymentId = editId,
                packageName = packageName,
                track = toTrack,
                versionCode = versionCode,
                message = "Successfully promoted from $fromTrack to $toTrack"
            )
            
        } catch (e: Exception) {
            logger.error("Failed to promote release", e)
            PlayStoreDeploymentResult(
                success = false,
                deploymentId = null,
                packageName = packageName,
                track = toTrack,
                versionCode = versionCode,
                message = "Promotion failed: ${e.message}",
                error = e
            )
        }
    }
    
    /**
     * Update app metadata
     */
    suspend fun updateAppMetadata(
        packageName: String,
        title: String? = null,
        shortDescription: String? = null,
        fullDescription: String? = null
    ): Boolean {
        logger.info("Updating metadata for: $packageName")
        
        return try {
            val editRequest = publisher.edits().insert(packageName, null)
            val edit = editRequest.execute()
            val editId = edit.id
            
            // Get current listing for default language
            val listings = publisher.edits().listings().list(packageName, editId).execute()
            val defaultLanguage = listings?.listings?.firstOrNull()?.language ?: "en-US"
            
            val currentListing = publisher.edits().listings().get(packageName, editId, defaultLanguage).execute()
                ?: com.google.api.services.androidpublisher.model.Listing()
            
            // Update fields if provided
            title?.let { currentListing.title = it }
            shortDescription?.let { currentListing.shortDescription = it }
            fullDescription?.let { currentListing.fullDescription = it }
            
            // Update the listing
            publisher.edits().listings().update(packageName, editId, defaultLanguage, currentListing).execute()
            
            // Commit the changes
            publisher.edits().commit(packageName, editId).execute()
            
            logger.info("Successfully updated metadata for $packageName")
            true
            
        } catch (e: Exception) {
            logger.error("Failed to update metadata for $packageName", e)
            false
        }
    }
    
    private fun determineAppStatus(tracks: List<com.google.api.services.androidpublisher.model.Track>?): String {
        val hasProduction = tracks?.any { it.track == "production" && !it.releases.isNullOrEmpty() }
        return if (hasProduction == true) "published" else "draft"
    }
    
    private fun getCurrentVersionCode(tracks: List<com.google.api.services.androidpublisher.model.Track>?): Long {
        return tracks?.flatMap { it.releases ?: emptyList() }
            ?.flatMap { it.versionCodes ?: emptyList() }
            ?.maxOrNull() ?: 0
    }
    
    private fun getCurrentVersionName(tracks: List<com.google.api.services.androidpublisher.model.Track>?): String {
        // Version name is not directly available from tracks, would need to be fetched from APK details
        return "Unknown"
    }
}

/**
 * Exception thrown by Play Store operations
 */
class PlayStoreException(message: String, cause: Throwable? = null) : Exception(message, cause)