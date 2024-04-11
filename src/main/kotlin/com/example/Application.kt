package com.example

import com.example.plugins.*
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator
import io.opentelemetry.context.propagation.ContextPropagators
import io.opentelemetry.context.propagation.TextMapPropagator
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter
import io.opentelemetry.instrumentation.ktor.v2_0.server.KtorServerTracing
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.resources.Resource
import io.opentelemetry.sdk.trace.SdkTracerProvider
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor
import io.opentelemetry.sdk.trace.samplers.Sampler
import io.opentelemetry.semconv.ResourceAttributes
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

fun main() {
    embeddedServer(Netty, port = 7331, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val serviceName = "otel-debug"
    val endpoint = "http://localhost:4317/"
    val sampler = Sampler.parentBased(Sampler.alwaysOn())

    val resource: Resource = Resource.getDefault()
        .merge(
            Resource.create(
                Attributes.of(
                    ResourceAttributes.SERVICE_NAME,
                    serviceName,
                    ResourceAttributes.SERVICE_VERSION,
                    "1.0"
                )
            )
        )


    val spanExporter = OtlpGrpcSpanExporter.builder()
        .setEndpoint(endpoint)
        .build()

    val sdkTracerProvider = SdkTracerProvider.builder()
        .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
        .setResource(resource)
        .setSampler(sampler)
        .build()

    val openTelemetry = OpenTelemetrySdk.builder().apply {
        setTracerProvider(sdkTracerProvider)
        setPropagators(
            ContextPropagators.create(
                TextMapPropagator.composite(
                    W3CTraceContextPropagator.getInstance(),
                    W3CBaggagePropagator.getInstance()
                )
            )
        )
    }.build()

    val httpClient = HttpClient(OkHttp)

    install(KtorServerTracing) {
        setOpenTelemetry(openTelemetry)
    }

    configureMonitoring()
    configureRouting(httpClient)
}
