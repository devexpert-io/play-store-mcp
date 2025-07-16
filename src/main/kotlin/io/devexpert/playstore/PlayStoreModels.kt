package io.devexpert.playstore

import kotlinx.serialization.Serializable

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
    val defaultTrack: String = "internal"
)