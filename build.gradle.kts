plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.shadow)
    application
}

group = "io.devexpert"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("io.devexpert.MainKt")
}

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
    
    // IO
    implementation(libs.kotlinx.io.core)
    
    // Logging
    implementation(libs.slf4j.api)
    implementation(libs.logback.classic)

    implementation(libs.google.play.console)
    implementation(libs.google.auth.library)
    
    // Testing
    testImplementation(libs.kotlin.test)
    testImplementation(libs.junit.jupiter)
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    archiveBaseName.set("play-store-mcp")
    archiveVersion.set("")
    archiveClassifier.set("all")
}