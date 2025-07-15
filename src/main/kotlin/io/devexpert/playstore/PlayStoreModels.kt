package io.devexpert.playstore

import kotlinx.serialization.Serializable

/**
 * Represents a Play Store application
 */
@Serializable
data class PlayStoreApp(
    val packageName: String,
    val name: String,
    val status: String, // published, draft, etc.
    val currentVersionCode: Long,
    val currentVersionName: String,
    val lastUpdated: Long
)

/**
 * Represents a Play Store release
 */
@Serializable
data class PlayStoreRelease(
    val packageName: String,
    val track: String, // internal, alpha, beta, production
    val status: String, // completed, inProgress, draft, halted
    val versionCode: Long,
    val rolloutPercentage: Int,
    val startTime: Long,
    val completedTime: Long?
)

/**
 * Represents app statistics from Play Store
 */
@Serializable
data class PlayStoreStats(
    val packageName: String,
    val downloads: Downloads,
    val ratings: Ratings,
    val crashes: Crashes,
    val reportPeriod: ReportPeriod
) {
    @Serializable
    data class Downloads(
        val total: Long,
        val last30Days: Long,
        val last7Days: Long
    )
    
    @Serializable
    data class Ratings(
        val average: Double,
        val totalRatings: Long,
        val distribution: RatingDistribution
    )
    
    @Serializable
    data class RatingDistribution(
        val fiveStars: Long,
        val fourStars: Long,
        val threeStars: Long,
        val twoStars: Long,
        val oneStar: Long
    )
    
    @Serializable
    data class Crashes(
        val crashRate: Double,
        val last30Days: Long
    )
    
    @Serializable
    data class ReportPeriod(
        val start: Long,
        val end: Long
    )
}

/**
 * Result of a deployment operation
 */
data class PlayStoreDeploymentResult(
    val success: Boolean,
    val deploymentId: String?,
    val packageName: String,
    val track: String,
    val versionCode: Long,
    val message: String,
    val error: Throwable? = null
)

/**
 * Configuration for Play Store API client
 */
data class PlayStoreConfig(
    val serviceAccountKeyPath: String,
    val applicationName: String = "Play Store MCP Server",
    val packageNames: List<String> = emptyList(), // Apps to monitor
    val defaultTrack: String = "internal",
    val enableMockMode: Boolean = false // Fallback to mock data if API fails
)

/**
 * Server configuration information
 */
@Serializable
data class ServerConfig(
    val serverInfo: ServerInfo,
    val playStoreConfig: PlayStoreConfigInfo,
    val capabilities: Capabilities,
    val environment: Environment,
    val timestamp: Long
) {
    @Serializable
    data class ServerInfo(
        val name: String,
        val version: String,
        val protocol: String,
        val transport: String
    )
    
    @Serializable
    data class PlayStoreConfigInfo(
        val apiVersion: String,
        val serviceAccountConfigured: Boolean,
        val defaultTrack: String,
        val supportedFormats: List<String>,
        val monitoredApps: Int
    )
    
    @Serializable
    data class Capabilities(
        val resources: Boolean,
        val tools: Boolean,
        val prompts: Boolean,
        val logging: Boolean
    )
    
    @Serializable
    data class Environment(
        val javaVersion: String,
        val kotlinVersion: String,
        val platform: String
    )
}