package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.event.*

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val userAgent = call.request.headers["User-Agent"]
            val traceparent = call.request.headers["traceparent"]
            "Status: $status, HTTP method: $httpMethod, User agent: $userAgent, Traceparent: $traceparent"
        }

        filter { call -> call.request.path().startsWith("/") }
    }
}
