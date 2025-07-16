package io.devexpert.playstore

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.io.File
import java.time.Instant

/**
 * Service for managing Play Store operations with fallback to mock data
 */
class PlayStoreService(private val config: PlayStoreConfig) {
    private val logger = LoggerFactory.getLogger(PlayStoreService::class.java)

    private val playStoreClient: PlayStoreClient = initializeClient()

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    init {
        logger.info("Initializing Play Store Service...")
    }

    private fun initializeClient(): PlayStoreClient {
        val keyFile = File(config.serviceAccountKeyPath)
        if (!keyFile.exists()) {
            throw IllegalArgumentException("Service account key not found: ${config.serviceAccountKeyPath}")
        }

        return try {
            PlayStoreClient(config.serviceAccountKeyPath, config.applicationName)
        } catch (e: Exception) {
            logger.error("Failed to initialize Play Store client", e)
            throw e
        }
    }

    /**
     * Get release status for a specific package
     */
    suspend fun getReleases(packageName: String): String = withContext(Dispatchers.IO) {
        logger.debug("Fetching releases for package: $packageName")

        val releases = playStoreClient.getReleases(packageName)

        val result = mapOf(
            "packageName" to packageName,
            "releases" to releases,
            "summary" to mapOf(
                "totalReleases" to releases.size,
                "activeReleases" to releases.count { it.status == "inProgress" },
                "completedReleases" to releases.count { it.status == "completed" }
            ),
            "lastUpdate" to Instant.now().toString()
        )

        json.encodeToString(
            kotlinx.serialization.json.JsonObject.serializer(),
            kotlinx.serialization.json.JsonObject(result.mapValues {
                kotlinx.serialization.json.JsonPrimitive(it.value.toString())
            })
        )
    }

    /**
     * Deploy an app
     */
    suspend fun deployApp(
        packageName: String,
        track: String,
        apkPath: String,
        versionCode: Long,
        releaseNotes: String?
    ): PlayStoreDeploymentResult = withContext(Dispatchers.IO) {
        logger.info("Deploying app: $packageName to $track")
        playStoreClient.deployApp(packageName, track, apkPath, versionCode, releaseNotes)
    }

    /**
     * Promote a release
     */
    suspend fun promoteRelease(
        packageName: String,
        fromTrack: String,
        toTrack: String,
        versionCode: Long
    ): PlayStoreDeploymentResult = withContext(Dispatchers.IO) {
        logger.info("Promoting release: $packageName from $fromTrack to $toTrack")
        playStoreClient.promoteRelease(packageName, fromTrack, toTrack, versionCode)
    }


}