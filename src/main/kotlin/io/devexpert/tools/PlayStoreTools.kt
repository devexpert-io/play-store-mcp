package io.devexpert.tools

import io.devexpert.playstore.PlayStoreService
import io.modelcontextprotocol.kotlin.sdk.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.TextContent
import io.modelcontextprotocol.kotlin.sdk.Tool
import io.modelcontextprotocol.kotlin.sdk.server.Server
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.slf4j.LoggerFactory
import java.time.Instant

class PlayStoreTools(private val playStoreService: PlayStoreService) {
    private val logger = LoggerFactory.getLogger(PlayStoreTools::class.java)

    fun registerTools(server: Server) {
        logger.info("Registering Play Store deployment tools...")

        server.addTool(
            name = "deploy_app",
            description = "Deploy a new version of an app to Play Store",
            inputSchema = Tool.Input(
                properties = buildJsonObject {
                    put("packageName", buildJsonObject {
                        put("type", JsonPrimitive("string"))
                        put("description", JsonPrimitive("Package name of the app (e.g., com.example.myapp)"))
                    })
                    put("track", buildJsonObject {
                        put("type", JsonPrimitive("string"))
                        put("description", JsonPrimitive("Release track: internal, alpha, beta, production"))
                        put("enum", kotlinx.serialization.json.JsonArray(listOf(
                            JsonPrimitive("internal"),
                            JsonPrimitive("alpha"), 
                            JsonPrimitive("beta"),
                            JsonPrimitive("production")
                        )))
                    })
                    put("apkPath", buildJsonObject {
                        put("type", JsonPrimitive("string"))
                        put("description", JsonPrimitive("Path to APK or AAB file"))
                    })
                    put("versionCode", buildJsonObject {
                        put("type", JsonPrimitive("integer"))
                        put("description", JsonPrimitive("Version code (must be higher than current)"))
                    })
                    put("releaseNotes", buildJsonObject {
                        put("type", JsonPrimitive("string"))
                        put("description", JsonPrimitive("Release notes for this version"))
                    })
                    put("rolloutPercentage", buildJsonObject {
                        put("type", JsonPrimitive("number"))
                        put("description", JsonPrimitive("Rollout percentage (0.0 to 1.0, default: 1.0 for full rollout)"))
                        put("minimum", JsonPrimitive(0.0))
                        put("maximum", JsonPrimitive(1.0))
                    })
                },
                required = listOf("packageName", "track", "apkPath", "versionCode")
            )
        ) { request ->
            val packageName = request.arguments.getArgument("packageName", "unknown")
            val track = request.arguments.getArgument("track", "internal")
            val apkPath = request.arguments.getArgument("apkPath", "")
            val versionCode = request.arguments.getArgument("versionCode", 1L)
            val releaseNotes = request.arguments.getArgument("releaseNotes", "No release notes provided")
            val rolloutPercentage = request.arguments.getArgument("rolloutPercentage", 1.0)

            logger.info("Deploy app tool called: $packageName to $track track with ${(rolloutPercentage * 100).toInt()}% rollout")

            val deploymentResult = runBlocking {
                playStoreService.deployApp(packageName, track, apkPath, versionCode, releaseNotes, rolloutPercentage)
            }

            val result = buildString {
                if (deploymentResult.success) {
                    appendLine("🚀 App Deployment Successful")
                    appendLine("================================")
                    appendLine("Package Name: $packageName")
                    appendLine("Track: $track")
                    appendLine("Version Code: $versionCode")
                    appendLine("Deployment ID: ${deploymentResult.deploymentId}")
                    appendLine("")
                    appendLine("✅ ${deploymentResult.message}")
                    appendLine("Started at: ${Instant.now()}")
                } else {
                    appendLine("❌ App Deployment Failed")
                    appendLine("================================")
                    appendLine("Package Name: $packageName")
                    appendLine("Track: $track")
                    appendLine("Version Code: $versionCode")
                    appendLine("")
                    appendLine("Error: ${deploymentResult.message}")
                    deploymentResult.error?.let { error ->
                        appendLine("Details: ${error.message}")
                    }
                }
            }

            CallToolResult(
                content = listOf(
                    TextContent(text = result)
                )
            )
        }

        server.addTool(
            name = "promote_release",
            description = "Promote a release from one track to another (e.g., alpha to beta)",
            inputSchema = Tool.Input(
                properties = buildJsonObject {
                    put("packageName", buildJsonObject {
                        put("type", JsonPrimitive("string"))
                        put("description", JsonPrimitive("Package name of the app"))
                    })
                    put("fromTrack", buildJsonObject {
                        put("type", JsonPrimitive("string"))
                        put("description", JsonPrimitive("Source track"))
                        put("enum", kotlinx.serialization.json.JsonArray(listOf(
                            JsonPrimitive("internal"),
                            JsonPrimitive("alpha"), 
                            JsonPrimitive("beta")
                        )))
                    })
                    put("toTrack", buildJsonObject {
                        put("type", JsonPrimitive("string"))
                        put("description", JsonPrimitive("Target track"))
                        put("enum", kotlinx.serialization.json.JsonArray(listOf(
                            JsonPrimitive("alpha"), 
                            JsonPrimitive("beta"),
                            JsonPrimitive("production")
                        )))
                    })
                    put("versionCode", buildJsonObject {
                        put("type", JsonPrimitive("integer"))
                        put("description", JsonPrimitive("Version code to promote"))
                    })
                },
                required = listOf("packageName", "fromTrack", "toTrack", "versionCode")
            )
        ) { request ->
            val packageName = request.arguments.getArgument("packageName", "unknown")
            val fromTrack = request.arguments.getArgument("fromTrack", "internal")
            val toTrack = request.arguments.getArgument("toTrack", "alpha")
            val versionCode = request.arguments.getArgument("versionCode", 1L)

            logger.info("Promote release tool called: $packageName from $fromTrack to $toTrack")

            val promotionResult = runBlocking {
                playStoreService.promoteRelease(packageName, fromTrack, toTrack, versionCode)
            }

            val result = buildString {
                if (promotionResult.success) {
                    appendLine("⬆️ Release Promotion Successful")
                    appendLine("============================")
                    appendLine("Package Name: $packageName")
                    appendLine("Version Code: $versionCode")
                    appendLine("From Track: $fromTrack")
                    appendLine("To Track: $toTrack")
                    appendLine("Promotion ID: ${promotionResult.deploymentId}")
                    appendLine("")
                    appendLine("✅ ${promotionResult.message}")
                    appendLine("Completed at: ${Instant.now()}")
                } else {
                    appendLine("❌ Release Promotion Failed")
                    appendLine("============================")
                    appendLine("Package Name: $packageName")
                    appendLine("Version Code: $versionCode")
                    appendLine("From Track: $fromTrack")
                    appendLine("To Track: $toTrack")
                    appendLine("")
                    appendLine("Error: ${promotionResult.message}")
                    promotionResult.error?.let { error ->
                        appendLine("Details: ${error.message}")
                    }
                }
            }

            CallToolResult(
                content = listOf(
                    TextContent(text = result)
                )
            )
        }

        server.addTool(
            name = "get_releases",
            description = "Get current status of app releases and deployments for a specific package",
            inputSchema = Tool.Input(
                properties = buildJsonObject {
                    put("packageName", buildJsonObject {
                        put("type", JsonPrimitive("string"))
                        put("description", JsonPrimitive("Package name of the app (e.g., com.example.myapp)"))
                    })
                },
                required = listOf("packageName")
            )
        ) { request ->
            val packageName = request.arguments.getArgument("packageName", "")
            if (packageName.isBlank()) {
                throw IllegalArgumentException("packageName is required")
            }

            logger.info("Get releases tool called for package: $packageName")

            val releasesJson = runBlocking {
                playStoreService.getReleases(packageName)
            }

            CallToolResult(
                content = listOf(
                    TextContent(text = releasesJson)
                )
            )
        }

        logger.info("Play Store tools registered successfully: deploy_app, promote_release, get_releases")
    }
}

private inline fun <reified T> Map<String, Any>.getArgument(key: String, defaultValue: T): T {
    return (this[key] as? JsonPrimitive)?.content?.let {
        when (T::class) {
            String::class -> it as T
            Long::class -> it.toLongOrNull() as? T
            Double::class -> it.toDoubleOrNull() as? T
            else -> null
        }
    } ?: defaultValue
}