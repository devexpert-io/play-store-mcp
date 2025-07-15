# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview
This is a Kotlin JVM project that implements an MCP (Model Context Protocol) server for Google Play Store console operations. The server enables deployment and management of Android applications through the Play Store API. Built with Gradle and designed for scalability and maintainability.

### Key Features
- MCP server implementation using the Kotlin MCP SDK
- Google Play Store API integration for app deployment
- Initially supports STDIO protocol with extensible architecture for additional MCP protocols
- Modular design for easy maintenance and feature expansion

## Development Commands

### Build and Test
- `./gradlew build` - Builds the project and runs tests
- `./gradlew clean` - Cleans the build directory
- `./gradlew test` - Runs the test suite
- `./gradlew check` - Runs all checks including tests

### Compilation
- `./gradlew compileKotlin` - Compiles main Kotlin source
- `./gradlew compileTestKotlin` - Compiles test Kotlin source

### Other Useful Commands
- `./gradlew tasks` - Lists all available Gradle tasks
- `./gradlew dependencies` - Shows project dependencies
- `./gradlew jar` - Creates a JAR file

## Project Structure
```
src/
├── main/
│   ├── kotlin/
│   │   ├── mcp/           # MCP protocol implementation
│   │   ├── playstore/     # Play Store API integration
│   │   ├── transport/     # Transport layer (STDIO, future protocols)
│   │   └── main.kt        # Application entry point
│   └── resources/         # Configuration and resources
└── test/
    ├── kotlin/            # Test source code
    └── resources/         # Test resources
```

## Configuration
- **Build file**: `build.gradle.kts` (Kotlin DSL)
- **Kotlin version**: 2.1.21
- **Test framework**: JUnit Platform
- **Code style**: Official Kotlin code style (set in `gradle.properties`)

## Dependencies
- **MCP Kotlin SDK**: For implementing MCP server functionality
- **Google Play Store API**: For Android app deployment operations
- **Kotlin Coroutines**: For asynchronous operations
- **Kotlin Serialization**: For JSON handling
- **SLF4J + Logback**: For logging
- **kotlin("test")**: For testing

All dependencies are managed through the `build.gradle.kts` file.

## Architecture Notes
- **Transport Layer**: Abstracted to support multiple MCP protocols (STDIO initially, extensible for others)
- **Play Store Integration**: Modular design for different Play Store operations
- **MCP Protocol**: Clean separation between MCP protocol handling and business logic
- **Error Handling**: Comprehensive error handling for API failures and protocol issues
- **Configuration**: Externalized configuration for API credentials and settings

## Git Commit Guidelines
- **NO** añadir coautoría de Claude en los commits
- Usar mensajes de commit claros y concisos
- Seguir conventional commits cuando sea apropiado