package com.example.plugins

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(httpClient: HttpClient) {
    routing {
        get("/") {
            // This line will make the bug happen. Replace with the commented line to avoid it.
            val response = httpClient.get("http://ip.jsontest.com").bodyAsText()
            // val response = "Test response, no HttpClient used"
            call.respondText("Hello World!")
            call.respondText(response)
        }
    }
}
