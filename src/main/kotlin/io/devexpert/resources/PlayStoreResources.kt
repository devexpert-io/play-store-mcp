package io.devexpert.resources

import io.devexpert.playstore.PlayStoreService
import io.modelcontextprotocol.kotlin.sdk.ReadResourceResult
import io.modelcontextprotocol.kotlin.sdk.TextResourceContents
import io.modelcontextprotocol.kotlin.sdk.server.Server
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

class PlayStoreResources(private val playStoreService: PlayStoreService) {
    private val logger = LoggerFactory.getLogger(PlayStoreResources::class.java)

    fun registerResources(server: Server) {
        logger.info("Registering Play Store MCP resources...")

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

        logger.info("Play Store resources registered successfully")
    }
}