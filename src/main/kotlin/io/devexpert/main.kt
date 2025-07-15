package io.devexpert

import io.devexpert.mcp.PlayStoreMcpServer
import io.devexpert.transport.StdioTransport
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

private val logger = LoggerFactory.getLogger("Main")

fun main() {
    logger.info("Starting Play Store MCP Server...")
    
    try {
        val mcpServer = PlayStoreMcpServer()
        mcpServer.initialize()
        
        val server = mcpServer.getServer()
        logger.info("Play Store MCP Server initialized successfully")
        
        val transport = StdioTransport()
        transport.startServer(server)
        
    } catch (e: Exception) {
        logger.error("Failed to start Play Store MCP Server", e)
        exitProcess(1)
    }
}