plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

group = "io.devexpert"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // MCP Core
    implementation(libs.mcp.kotlin.sdk)
    
    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.core)
    
    // Serialization
    implementation(libs.kotlinx.serialization.json)
    
    // Logging
    implementation(libs.slf4j.api)
    implementation(libs.logback.classic)
    
    // Google Play Console API
    implementation(libs.google.play.console)
    
    // Testing
    testImplementation(libs.kotlin.test)
    testImplementation(libs.junit.jupiter)
}

tasks.test {
    useJUnitPlatform()
}