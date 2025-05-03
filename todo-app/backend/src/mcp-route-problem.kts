
@file:DependsOn("io.ktor:ktor-server-core-jvm:3.1.2")
@file:DependsOn("io.ktor:ktor-server-netty-jvm:3.1.2")
@file:DependsOn("io.ktor:ktor-server-sse:3.1.2")
@file:DependsOn("io.modelcontextprotocol:kotlin-sdk:0.5.0")

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.sse.*
import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.mcp

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(SSE)
        
        routing {
            route("myRoute") {
                mcp {
                    Server(
                        serverInfo = Implementation(
                            name = "example-sse-server",
                            version = "1.0.0"
                        ),
                        options = ServerOptions(
                            capabilities = ServerCapabilities(
                                prompts = ServerCapabilities.Prompts(listChanged = null),
                                resources = ServerCapabilities.Resources(subscribe = null, listChanged = null)
                            )
                        )
                    )
                }
            }
        }
    }.start(wait = true)
}