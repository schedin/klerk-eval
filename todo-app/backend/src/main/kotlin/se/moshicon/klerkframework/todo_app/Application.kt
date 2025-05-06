package se.moshicon.klerkframework.todo_app

import dev.klerkframework.klerk.Klerk
import dev.klerkframework.mcp.createMcpServer
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.http.*
import io.modelcontextprotocol.kotlin.sdk.server.mcp

import kotlinx.coroutines.runBlocking
import se.moshicon.klerkframework.todo_app.http.configureHttpRouting
import se.moshicon.klerkframework.todo_app.http.findOrCreateUser
import se.moshicon.klerkframework.todo_app.users.*

fun main() {
    val klerk = Klerk.create(createConfig())
    runBlocking {
        klerk.meta.start()
        createInitialUsers(klerk)
    }

    suspend fun contextProvider(): Ctx {
        val user = findOrCreateUser(klerk, "Alice")
        return Ctx(GroupModelIdentity(model = user, groups = listOf("admins", "users")))
    }


    val mcpServer = createMcpServer(klerk, ::contextProvider, "TODO application", "1.0.0")
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
        configureHttpRouting(klerk)

//        install(SSE)
//        routing {
//            route("myRoute") {
//                mcp {
//                    getMcpServer()
//                }
//            }
//        }
        //Due do a bug in kotlin-sdk for MCP (https://github.com/modelcontextprotocol/kotlin-sdk/issues/94) it is
        // currently not possible to control the URL for the MCP server.
        mcp {
            mcpServer
        }


    }.start(wait = true)
}

