package io.devexpert.tools

import io.modelcontextprotocol.kotlin.sdk.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.TextContent
import io.modelcontextprotocol.kotlin.sdk.Tool
import io.modelcontextprotocol.kotlin.sdk.server.Server
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.slf4j.LoggerFactory
import java.time.Instant

class TestTools {
    private val logger = LoggerFactory.getLogger(TestTools::class.java)

    fun registerTools(server: Server) {
        logger.info("Registering test tools...")
        
        // Tool 1: Ping - Simple connectivity test
        server.addTool(
            name = "ping",
            description = "Simple ping tool to test MCP server connectivity",
            inputSchema = Tool.Input(
                properties = buildJsonObject {}
            )
        ) { request ->
            logger.info("Ping tool called")
            CallToolResult(
                content = listOf(
                    TextContent(
                        text = "Pong! MCP Server is running at ${Instant.now()}"
                    )
                )
            )
        }
        
        // Tool 2: Echo - Test parameter passing
        server.addTool(
            name = "echo",
            description = "Echo tool that repeats the input message",
            inputSchema = Tool.Input(
                properties = buildJsonObject {
                    put("message", buildJsonObject {
                        put("type", JsonPrimitive("string"))
                        put("description", JsonPrimitive("Message to echo back"))
                    })
                },
                required = listOf("message")
            )
        ) { request ->
            val message = request.arguments["message"]?.let {
                if (it is JsonPrimitive) it.content else it.toString() 
            } ?: "No message provided"
            logger.info("Echo tool called with message: $message")
            
            CallToolResult(
                content = listOf(
                    TextContent(
                        text = "Echo: $message"
                    )
                )
            )
        }
        
        // Tool 3: Server Info - Test server information
        server.addTool(
            name = "server_info",
            description = "Get information about the MCP server",
            inputSchema = Tool.Input(
                properties = buildJsonObject {}
            )
        ) { request ->
            logger.info("Server info tool called")
            
            val serverInfo = buildString {
                appendLine("Play Store MCP Server Information:")
                appendLine("- Name: play-store-mcp")
                appendLine("- Version: 1.0.0")
                appendLine("- Protocol: MCP (Model Context Protocol)")
                appendLine("- Transport: STDIO")
                appendLine("- Capabilities: Resources, Tools, Prompts, Logging")
                appendLine("- Status: Running")
                appendLine("- Timestamp: ${Instant.now()}")
            }
            
            CallToolResult(
                content = listOf(
                    TextContent(
                        text = serverInfo
                    )
                )
            )
        }
        
        logger.info("Test tools registered successfully: ping, echo, server_info")
    }
}