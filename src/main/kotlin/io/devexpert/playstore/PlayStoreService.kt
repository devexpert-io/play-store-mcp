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

    private val playStoreClient: PlayStoreClient? = initializeClient()

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    init {
        logger.info("Initializing Play Store Service...")
        logger.info("Mock mode: ${config.enableMockMode}")
        logger.info("Configured apps: ${config.packageNames.size}")
    }

    private fun initializeClient(): PlayStoreClient? {
        if (config.enableMockMode) {
            logger.info("Mock mode enabled - skipping API client initialization")
            return null
        }

        return try {
            val keyFile = File(config.serviceAccountKeyPath)
            if (!keyFile.exists()) {
                logger.warn("Service account key not found: ${config.serviceAccountKeyPath}")
                logger.warn("Falling back to mock mode")
                return null
            }

            PlayStoreClient(config.serviceAccountKeyPath, config.applicationName)
        } catch (e: Exception) {
            logger.error("Failed to initialize Play Store client - using mock mode", e)
            null
        }
    }

    /**
     * Get release status
     */
    suspend fun getReleases(): String = withContext(Dispatchers.IO) {
        logger.debug("Fetching releases...")

        if (playStoreClient == null) {
            return@withContext getMockReleases()
        }

        try {
            val allReleases = mutableListOf<PlayStoreRelease>()

            for (packageName in config.packageNames) {
                val releases = playStoreClient.getReleases(packageName)
                allReleases.addAll(releases)
            }

            if (allReleases.isEmpty()) {
                logger.warn("No releases found - falling back to mock data")
                return@withContext getMockReleases()
            }

            val result = mapOf(
                "releases" to allReleases,
                "summary" to mapOf(
                    "totalReleases" to allReleases.size,
                    "activeReleases" to allReleases.count { it.status == "inProgress" },
                    "completedReleases" to allReleases.count { it.status == "completed" }
                ),
                "lastUpdate" to Instant.now().toString()
            )

            json.encodeToString(
                kotlinx.serialization.json.JsonObject.serializer(),
                kotlinx.serialization.json.JsonObject(result.mapValues {
                    kotlinx.serialization.json.JsonPrimitive(it.value.toString())
                })
            )

        } catch (e: Exception) {
            logger.error("Failed to fetch releases from API - using mock data", e)
            getMockReleases()
        }
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

        if (playStoreClient == null) {
            logger.info("Mock mode - simulating deployment")
            return@withContext PlayStoreDeploymentResult(
                success = true,
                deploymentId = "mock-${System.currentTimeMillis()}",
                packageName = packageName,
                track = track,
                versionCode = versionCode,
                message = "Mock deployment successful - API integration not configured"
            )
        }

        return@withContext playStoreClient.deployApp(packageName, track, apkPath, versionCode, releaseNotes)
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

        if (playStoreClient == null) {
            logger.info("Mock mode - simulating promotion")
            return@withContext PlayStoreDeploymentResult(
                success = true,
                deploymentId = "mock-promotion-${System.currentTimeMillis()}",
                packageName = packageName,
                track = toTrack,
                versionCode = versionCode,
                message = "Mock promotion successful - API integration not configured"
            )
        }

        return@withContext playStoreClient.promoteRelease(packageName, fromTrack, toTrack, versionCode)
    }

    private fun getMockReleases(): String {
        return """
        {
          "releases": [
            {
              "packageName": "com.example.myapp",
              "track": "production",
              "status": "completed",
              "versionCode": 42,
              "rolloutPercentage": 100,
              "startTime": "${Instant.now().minusSeconds(7200)}",
              "completedTime": "${Instant.now().minusSeconds(3600)}"
            },
            {
              "packageName": "com.example.anotherapp",
              "track": "internal",
              "status": "inProgress", 
              "versionCode": 16,
              "rolloutPercentage": 25,
              "startTime": "${Instant.now().minusSeconds(1800)}",
              "completedTime": null
            }
          ],
          "summary": {
            "totalReleases": 2,
            "activeReleases": 1,
            "completedReleases": 1
          },
          "lastUpdate": "${Instant.now()}"
        }
        """.trimIndent()
    }

}