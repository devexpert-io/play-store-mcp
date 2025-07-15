package io.devexpert.resources

import io.devexpert.playstore.PlayStoreService
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.ReadResourceResult
import io.modelcontextprotocol.kotlin.sdk.TextResourceContents
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

class PlayStoreResources(private val playStoreService: PlayStoreService) {
    private val logger = LoggerFactory.getLogger(PlayStoreResources::class.java)

    fun registerResources(server: Server) {
        logger.info("Registering Play Store MCP resources...")

        // Resource 1: App List - List of published applications
        server.addResource(
            uri = "playstore://apps",
            name = "Published Apps",
            description = "List of all published applications in Play Store",
            mimeType = "application/json"
        ) { request ->
            logger.info("App list resource requested")
            
            val appListJson = runBlocking {
                playStoreService.getApplications()
            }

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

        // Resource 2: Release Status - Current status of releases and deployments
        server.addResource(
            uri = "playstore://releases",
            name = "Release Status",
            description = "Current status of app releases and deployments",
            mimeType = "application/json"
        ) { request ->
            logger.info("Release status resource requested")
            
            val releaseStatusJson = runBlocking {
                playStoreService.getReleases()
            }

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

        // Resource 3: App Statistics - Download and performance statistics
        server.addResource(
            uri = "playstore://stats",
            name = "App Statistics",
            description = "Download and performance statistics for published apps",
            mimeType = "application/json"
        ) { request ->
            logger.info("App statistics resource requested")
            
            val statsJson = runBlocking {
                playStoreService.getStatistics()
            }

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

        // Resource 4: Configuration - Current server configuration settings
        server.addResource(
            uri = "playstore://config",
            name = "Server Configuration",
            description = "Current configuration settings for Play Store MCP server",
            mimeType = "application/json"
        ) { request ->
            logger.info("Server configuration resource requested")
            
            val configJson = runBlocking {
                playStoreService.getServerConfig()
            }

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