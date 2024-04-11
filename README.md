
# Reproduction of bug in OpenTelemetry Ktor server instrumentation

## What is the bug?

For some reason, when using `KtorServerTracing` in combination with a route that uses Ktor's `HttpClient`,
there seems to be context leak, which causes the [shouldSuppress](https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/955470a63a49c035b426cb6b52e98a5fb67d1e2a/instrumentation-api/src/main/java/io/opentelemetry/instrumentation/api/instrumenter/Instrumenter.java#L111) function to return true.
That function is [called from here.](https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/955470a63a49c035b426cb6b52e98a5fb67d1e2a/instrumentation/ktor/ktor-2.0/library/src/main/kotlin/io/opentelemetry/instrumentation/ktor/v2_0/server/KtorServerTracing.kt#L89)
## How to reproduce?

1. Clone this repo
2. Run the application with `./gradlew run`
3. Also, run some service that collects traces on localhost:4317, such as Jaeger UI or [otel-desktop-viewer](https://github.com/CtrlSpice/otel-desktop-viewer)
4. Run the requests in Queries.http
5. Observe that you only get one trace, since we have limited the number of threads to one. If you change the limit [here](https://github.com/LangdalP/ktor-otel-debug/blob/main/build.gradle.kts#L24) to two and redo the steps above, you will instead see two traces. And so on.

