val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "1.9.23"
    id("io.ktor.plugin") version "2.3.9"
}

kotlin {
    jvmToolchain(17)
}

group = "com.example"
version = "0.0.1"

application {
    mainClass.set("com.example.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")

    applicationDefaultJvmArgs = listOf(
        "-Dio.ktor.development=$isDevelopment",
        // The below setting makes the bug more visible, since the otel context seems to be reused per thread
        "-XX:ActiveProcessorCount=1"
    )
}

repositories {
    mavenCentral()
}

val otelVerSdk = "1.36.0"
val otelVer = "2.2.0-alpha"

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.opentelemetry:opentelemetry-sdk:$otelVerSdk")
    implementation("io.opentelemetry:opentelemetry-api:$otelVerSdk")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp:$otelVerSdk")
    implementation("io.opentelemetry.instrumentation:opentelemetry-ktor-2.0:$otelVer")
    implementation("io.opentelemetry:opentelemetry-extension-kotlin:$otelVerSdk")
    implementation("io.ktor:ktor-server-call-logging-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-client-core-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-client-okhttp-jvm:2.3.9")
    // implementation("io.ktor:ktor-client-logging-jvm:2.3.9")
    // implementation("io.ktor:ktor-client-content-negotiation-jvm:2.3.9")
    // implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.9")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
