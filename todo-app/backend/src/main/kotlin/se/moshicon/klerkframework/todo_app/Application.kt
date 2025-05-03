package se.moshicon.klerkframework.todo_app

import dev.klerkframework.klerk.Klerk
import dev.klerkframework.mcp.getMcpServer
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.sse.SSE
import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.mcp
import io.ktor.server.routing.routing
import io.ktor.server.routing.route

import kotlinx.coroutines.runBlocking
import se.moshicon.klerkframework.todo_app.http.configureHttpRouting
import se.moshicon.klerkframework.todo_app.users.*

fun main() {
    val klerk = Klerk.create(createConfig())
    runBlocking {
        klerk.meta.start()
        createInitialUsers(klerk)
    }
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        // Configure CORS to allow frontend requests
        install(CORS) {
            allowMethod(HttpMethod.Options)
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Put)
            allowMethod(HttpMethod.Delete)
            allowMethod(HttpMethod.Patch)
            allowHeader(HttpHeaders.Authorization)
            allowHeader(HttpHeaders.ContentType)
            allowHeader(HttpHeaders.AccessControlAllowOrigin)
            allowCredentials = true
            anyHost() // For development only - restrict in production
        }

        // Configure JSON serialization
        install(ContentNegotiation) {
            json()
        }

//        install(SSE)
//        configureHttpRouting(klerk)

        mcp {
            getMcpServer()
        }

//        install(SSE)
//        routing {
//            route("myRoute") {
//                mcp {
//                    getMcpServer()
//                }
//            }
//        }

    }.start(wait = true)
}
