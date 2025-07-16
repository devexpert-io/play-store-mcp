package io.devexpert.mcp

import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.devexpert.tools.PlayStoreTools
import io.devexpert.resources.PlayStoreResources
import io.devexpert.playstore.PlayStoreService
import io.devexpert.playstore.PlayStoreConfig
import org.slf4j.LoggerFactory

class PlayStoreMcpServer {
    private val logger = LoggerFactory.getLogger(PlayStoreMcpServer::class.java)
    
    private val serverInfo = Implementation(
        name = "play-store-mcp",
        version = "1.0.0"
    )
    
    private val serverCapabilities = ServerCapabilities(
        resources = ServerCapabilities.Resources(
            subscribe = true,
            listChanged = true
        ),
        tools = ServerCapabilities.Tools(
            listChanged = true
        ),
        prompts = ServerCapabilities.Prompts(
            listChanged = true
        )
    )
    
    private val serverOptions = ServerOptions(
        capabilities = serverCapabilities
    )
    
    private val server = Server(
        serverInfo = serverInfo,
        options = serverOptions
    )
    
    private val playStoreService: PlayStoreService by lazy {
        initializePlayStoreService()
    }

    fun getServer(): Server {
        logger.info("Initializing Play Store MCP Server v${serverInfo.version}")
        return server
    }
    
    private fun initializePlayStoreService(): PlayStoreService {
        logger.info("Initializing Play Store Service...")
        
        val config = PlayStoreConfig(
            serviceAccountKeyPath = System.getenv("PLAY_STORE_SERVICE_ACCOUNT_KEY_PATH") 
                ?: "service-account-key.json",
            applicationName = "Play Store MCP Server",
            packageNames = System.getenv("PLAY_STORE_PACKAGE_NAMES")?.split(",")?.map { it.trim() } 
                ?: emptyList(),
            defaultTrack = System.getenv("PLAY_STORE_DEFAULT_TRACK") ?: "internal"
        )
        
        logger.info("Play Store Configuration:")
        logger.info("- Service Account Key Path: ${config.serviceAccountKeyPath}")
        logger.info("- Application Name: ${config.applicationName}")
        logger.info("- Package Names: ${config.packageNames}")
        logger.info("- Default Track: ${config.defaultTrack}")
        
        return PlayStoreService(config)
    }
    
    fun initialize() {
        logger.info("Setting up Play Store MCP Server capabilities:")
        logger.info("- Resources: Subscribe=${serverCapabilities.resources?.subscribe}, ListChanged=${serverCapabilities.resources?.listChanged}")
        logger.info("- Tools: ListChanged=${serverCapabilities.tools?.listChanged}")
        logger.info("- Prompts: ListChanged=${serverCapabilities.prompts?.listChanged}")
        logger.info("- Logging: Enabled=${serverCapabilities.logging != null}")

        setupResources()
        setupTools()
    }
    
    private fun setupResources() {
        logger.debug("Setting up MCP resources...")
        val playStoreResources = PlayStoreResources(playStoreService)
        playStoreResources.registerResources(server)
    }
    
    private fun setupTools() {
        logger.debug("Setting up MCP tools...")
        
        // Register Play Store deployment tools
        val playStoreTools = PlayStoreTools(playStoreService)
        playStoreTools.registerTools(server)
    }
}