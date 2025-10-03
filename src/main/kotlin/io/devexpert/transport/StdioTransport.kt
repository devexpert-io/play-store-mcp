package io.devexpert.transport

import io.ktor.utils.io.streams.asInput
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.io.asSink
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.slf4j.LoggerFactory

class StdioTransport {
    private val logger = LoggerFactory.getLogger(StdioTransport::class.java)
    
    fun startServer(server: Server) {
        logger.info("Starting STDIO transport...")
        
        try {
            val inputSource = System.`in`.asInput()
            val outputSink = System.out.asSink().buffered()
            
            val transport = StdioServerTransport(
                inputStream = inputSource,
                outputStream = outputSink
            )
            logger.info("STDIO transport initialized successfully")
            
            runBlocking {
                logger.info("Connecting server to STDIO transport...")
                server.connect(transport)
                logger.info("Server connected successfully - ready to process MCP requests")
                val done = Job()
                server.onClose {
                    done.complete()
                }
                done.join()
            }
            
        } catch (e: Exception) {
            logger.error("Failed to start STDIO transport", e)
            throw e
        }
    }
}