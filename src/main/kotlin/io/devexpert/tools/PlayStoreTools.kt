package io.devexpert.tools

import io.modelcontextprotocol.kotlin.sdk.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.TextContent
import io.modelcontextprotocol.kotlin.sdk.Tool
import io.modelcontextprotocol.kotlin.sdk.server.Server
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.*

class PlayStoreTools {
    private val logger = LoggerFactory.getLogger(PlayStoreTools::class.java)

    fun registerTools(server: Server) {
        logger.info("Registering Play Store deployment tools...")

        // Tool 1: Deploy App - Deploy a new version of an app
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
                },
                required = listOf("packageName", "track", "apkPath", "versionCode")
            )
        ) { request ->
            val packageName = request.arguments["packageName"]?.let { 
                if (it is JsonPrimitive) it.content else it.toString() 
            } ?: "unknown"
            val track = request.arguments["track"]?.let { 
                if (it is JsonPrimitive) it.content else it.toString() 
            } ?: "internal"
            val apkPath = request.arguments["apkPath"]?.let { 
                if (it is JsonPrimitive) it.content else it.toString() 
            } ?: ""
            val versionCode = request.arguments["versionCode"]?.let { 
                if (it is JsonPrimitive) it.content.toIntOrNull() else null 
            } ?: 1
            val releaseNotes = request.arguments["releaseNotes"]?.let { 
                if (it is JsonPrimitive) it.content else it.toString() 
            } ?: "No release notes provided"

            logger.info("Deploy app tool called: $packageName to $track track")

            // Simulate deployment process
            val deploymentId = UUID.randomUUID().toString()
            val result = buildString {
                appendLine("🚀 App Deployment Started")
                appendLine("================================")
                appendLine("Package Name: $packageName")
                appendLine("Track: $track")
                appendLine("APK Path: $apkPath")
                appendLine("Version Code: $versionCode")
                appendLine("Release Notes: $releaseNotes")
                appendLine("")
                appendLine("Deployment Status:")
                appendLine("✅ APK uploaded successfully")
                appendLine("✅ Version code validated")
                appendLine("✅ Release created in $track track")
                appendLine("⏳ Rollout started (0% -> 5%)")
                appendLine("")
                appendLine("Deployment ID: $deploymentId")
                appendLine("Started at: ${Instant.now()}")
                appendLine("Estimated completion: ${Instant.now().plusSeconds(1800)}")
                appendLine("")
                appendLine("Note: This is a mock deployment. In real integration,")
                appendLine("this would upload to Google Play Console API.")
            }

            CallToolResult(
                content = listOf(
                    TextContent(text = result)
                )
            )
        }

        // Tool 2: Create Release - Create a new release without uploading
        server.addTool(
            name = "create_release",
            description = "Create a new release in Play Store without uploading binary",
            inputSchema = Tool.Input(
                properties = buildJsonObject {
                    put("packageName", buildJsonObject {
                        put("type", JsonPrimitive("string"))
                        put("description", JsonPrimitive("Package name of the app"))
                    })
                    put("track", buildJsonObject {
                        put("type", JsonPrimitive("string"))
                        put("description", JsonPrimitive("Release track"))
                        put("enum", kotlinx.serialization.json.JsonArray(listOf(
                            JsonPrimitive("internal"),
                            JsonPrimitive("alpha"), 
                            JsonPrimitive("beta"),
                            JsonPrimitive("production")
                        )))
                    })
                    put("releaseName", buildJsonObject {
                        put("type", JsonPrimitive("string"))
                        put("description", JsonPrimitive("Name for this release"))
                    })
                },
                required = listOf("packageName", "track", "releaseName")
            )
        ) { request ->
            val packageName = request.arguments["packageName"]?.let { 
                if (it is JsonPrimitive) it.content else it.toString() 
            } ?: "unknown"
            val track = request.arguments["track"]?.let { 
                if (it is JsonPrimitive) it.content else it.toString() 
            } ?: "internal"
            val releaseName = request.arguments["releaseName"]?.let { 
                if (it is JsonPrimitive) it.content else it.toString() 
            } ?: "Unnamed Release"

            logger.info("Create release tool called: $releaseName for $packageName")

            val releaseId = UUID.randomUUID().toString()
            val result = buildString {
                appendLine("📦 Release Created Successfully")
                appendLine("==============================")
                appendLine("Package Name: $packageName")
                appendLine("Track: $track")
                appendLine("Release Name: $releaseName")
                appendLine("Release ID: $releaseId")
                appendLine("Status: Draft")
                appendLine("Created at: ${Instant.now()}")
                appendLine("")
                appendLine("Next steps:")
                appendLine("1. Upload APK/AAB using deploy_app tool")
                appendLine("2. Add release notes")
                appendLine("3. Submit for review")
            }

            CallToolResult(
                content = listOf(
                    TextContent(text = result)
                )
            )
        }

        // Tool 3: Update App Metadata - Update app description, screenshots, etc.
        server.addTool(
            name = "update_app_metadata",
            description = "Update app metadata like description, screenshots, and store listing",
            inputSchema = Tool.Input(
                properties = buildJsonObject {
                    put("packageName", buildJsonObject {
                        put("type", JsonPrimitive("string"))
                        put("description", JsonPrimitive("Package name of the app"))
                    })
                    put("title", buildJsonObject {
                        put("type", JsonPrimitive("string"))
                        put("description", JsonPrimitive("App title"))
                    })
                    put("shortDescription", buildJsonObject {
                        put("type", JsonPrimitive("string"))
                        put("description", JsonPrimitive("Short description (max 80 chars)"))
                    })
                    put("fullDescription", buildJsonObject {
                        put("type", JsonPrimitive("string"))
                        put("description", JsonPrimitive("Full description"))
                    })
                },
                required = listOf("packageName")
            )
        ) { request ->
            val packageName = request.arguments["packageName"]?.let { 
                if (it is JsonPrimitive) it.content else it.toString() 
            } ?: "unknown"
            val title = request.arguments["title"]?.let { 
                if (it is JsonPrimitive) it.content else null 
            }
            val shortDescription = request.arguments["shortDescription"]?.let { 
                if (it is JsonPrimitive) it.content else null 
            }
            val fullDescription = request.arguments["fullDescription"]?.let { 
                if (it is JsonPrimitive) it.content else null 
            }

            logger.info("Update app metadata tool called for: $packageName")

            val result = buildString {
                appendLine("📝 App Metadata Updated")
                appendLine("=======================")
                appendLine("Package Name: $packageName")
                if (title != null) appendLine("Title: $title")
                if (shortDescription != null) appendLine("Short Description: $shortDescription")
                if (fullDescription != null) appendLine("Full Description: ${fullDescription.take(100)}...")
                appendLine("")
                appendLine("✅ Store listing updated successfully")
                appendLine("Updated at: ${Instant.now()}")
                appendLine("")
                appendLine("Note: Changes may take up to 2 hours to appear in Play Store")
            }

            CallToolResult(
                content = listOf(
                    TextContent(text = result)
                )
            )
        }

        // Tool 4: Promote Release - Promote a release from one track to another
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
            val packageName = request.arguments["packageName"]?.let { 
                if (it is JsonPrimitive) it.content else it.toString() 
            } ?: "unknown"
            val fromTrack = request.arguments["fromTrack"]?.let { 
                if (it is JsonPrimitive) it.content else it.toString() 
            } ?: "internal"
            val toTrack = request.arguments["toTrack"]?.let { 
                if (it is JsonPrimitive) it.content else it.toString() 
            } ?: "alpha"
            val versionCode = request.arguments["versionCode"]?.let { 
                if (it is JsonPrimitive) it.content.toIntOrNull() else null 
            } ?: 1

            logger.info("Promote release tool called: $packageName from $fromTrack to $toTrack")

            val promotionId = UUID.randomUUID().toString()
            val result = buildString {
                appendLine("⬆️ Release Promotion Started")
                appendLine("============================")
                appendLine("Package Name: $packageName")
                appendLine("Version Code: $versionCode")
                appendLine("From Track: $fromTrack")
                appendLine("To Track: $toTrack")
                appendLine("Promotion ID: $promotionId")
                appendLine("")
                appendLine("Status:")
                appendLine("✅ Version validated in source track")
                appendLine("✅ Target track prepared")
                appendLine("⏳ Promoting release...")
                appendLine("")
                appendLine("Estimated completion: ${Instant.now().plusSeconds(900)}")
                appendLine("")
                appendLine("The release will be available in $toTrack track once promotion completes.")
            }

            CallToolResult(
                content = listOf(
                    TextContent(text = result)
                )
            )
        }

        logger.info("Play Store tools registered successfully: deploy_app, create_release, update_app_metadata, promote_release")
    }
}