package io.devexpert.resources

import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.ReadResourceResult
import io.modelcontextprotocol.kotlin.sdk.TextResourceContents
import org.slf4j.LoggerFactory
import java.time.Instant

class PlayStoreResources {
    private val logger = LoggerFactory.getLogger(PlayStoreResources::class.java)

    fun registerResources(server: Server) {
        logger.info("Registering Play Store MCP resources...")

        // Resource 1: App List - Lista de aplicaciones publicadas
        server.addResource(
            uri = "playstore://apps",
            name = "Published Apps",
            description = "List of all published applications in Play Store",
            mimeType = "application/json"
        ) { request ->
            logger.info("App list resource requested")
            
            val appListJson = """
            {
              "apps": [
                {
                  "packageName": "com.example.myapp",
                  "name": "My Example App",
                  "status": "published",
                  "currentVersionCode": 42,
                  "currentVersionName": "2.1.0",
                  "lastUpdated": "${Instant.now().minusSeconds(86400)}"
                },
                {
                  "packageName": "com.example.anotherapp", 
                  "name": "Another Example App",
                  "status": "draft",
                  "currentVersionCode": 15,
                  "currentVersionName": "1.5.0",
                  "lastUpdated": "${Instant.now().minusSeconds(172800)}"
                }
              ],
              "totalApps": 2,
              "lastSync": "${Instant.now()}"
            }
            """.trimIndent()

            ReadResourceResult(
                contents = listOf(
                    TextResourceContents(
                        text = appListJson,
                        uri = request.uri,
                        mimeType = "application/json"
                    )
                )
            )
        }

        // Resource 2: Release Status - Estado de releases
        server.addResource(
            uri = "playstore://releases",
            name = "Release Status",
            description = "Current status of app releases and deployments",
            mimeType = "application/json"
        ) { request ->
            logger.info("Release status resource requested")
            
            val releaseStatusJson = """
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

            ReadResourceResult(
                contents = listOf(
                    TextResourceContents(
                        text = releaseStatusJson,
                        uri = request.uri,
                        mimeType = "application/json"
                    )
                )
            )
        }

        // Resource 3: App Statistics - Estadísticas de aplicaciones
        server.addResource(
            uri = "playstore://stats",
            name = "App Statistics",
            description = "Download and performance statistics for published apps",
            mimeType = "application/json"
        ) { request ->
            logger.info("App statistics resource requested")
            
            val statsJson = """
            {
              "statistics": [
                {
                  "packageName": "com.example.myapp",
                  "downloads": {
                    "total": 125000,
                    "last30Days": 15000,
                    "last7Days": 3500
                  },
                  "ratings": {
                    "average": 4.2,
                    "totalRatings": 2341,
                    "distribution": {
                      "5stars": 1200,
                      "4stars": 758,
                      "3stars": 234,
                      "2stars": 98,
                      "1stars": 51
                    }
                  },
                  "crashes": {
                    "crashRate": 0.02,
                    "last30Days": 45
                  }
                }
              ],
              "reportPeriod": {
                "start": "${Instant.now().minusSeconds(2592000)}",
                "end": "${Instant.now()}"
              },
              "generatedAt": "${Instant.now()}"
            }
            """.trimIndent()

            ReadResourceResult(
                contents = listOf(
                    TextResourceContents(
                        text = statsJson,
                        uri = request.uri,
                        mimeType = "application/json"
                    )
                )
            )
        }

        // Resource 4: Configuration - Configuración del servidor
        server.addResource(
            uri = "playstore://config",
            name = "Server Configuration",
            description = "Current configuration settings for Play Store MCP server",
            mimeType = "application/json"
        ) { request ->
            logger.info("Server configuration resource requested")
            
            val configJson = """
            {
              "serverInfo": {
                "name": "play-store-mcp",
                "version": "1.0.0",
                "protocol": "MCP",
                "transport": "STDIO"
              },
              "playStoreConfig": {
                "apiVersion": "v3",
                "serviceAccountConfigured": false,
                "defaultTrack": "internal",
                "supportedFormats": ["apk", "aab"]
              },
              "capabilities": {
                "resources": true,
                "tools": true,
                "prompts": true,
                "logging": true
              },
              "environment": {
                "javaVersion": "${System.getProperty("java.version")}",
                "kotlinVersion": "2.2.0"
              },
              "timestamp": "${Instant.now()}"
            }
            """.trimIndent()

            ReadResourceResult(
                contents = listOf(
                    TextResourceContents(
                        text = configJson,
                        uri = request.uri,
                        mimeType = "application/json"
                    )
                )
            )
        }

        logger.info("Play Store resources registered successfully: apps, releases, stats, config")
    }
}