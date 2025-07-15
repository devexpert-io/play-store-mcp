package io.devexpert

import io.devexpert.mcp.PlayStoreMcpServer
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

private val logger = LoggerFactory.getLogger("Main")

fun main() {
    logger.info("Starting Play Store MCP Server...")
    
    try {
        val mcpServer = PlayStoreMcpServer()
        mcpServer.initialize()

        logger.info("Play Store MCP Server initialized successfully")
        
        // TODO: Setup transport and start server
        logger.info("Server ready to accept connections")
        
    } catch (e: Exception) {
        logger.error("Failed to start Play Store MCP Server", e)
        exitProcess(1)
    }
}