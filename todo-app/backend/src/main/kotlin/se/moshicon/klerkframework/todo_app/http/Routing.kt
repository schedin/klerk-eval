package se.moshicon.klerkframework.todo_app.http

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import dev.klerkframework.klerk.CustomIdentity
import dev.klerkframework.klerk.Klerk
import dev.klerkframework.klerk.SystemIdentity
import dev.klerkframework.web.LowCodeConfig
import dev.klerkframework.web.LowCodeMain

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import se.moshicon.klerkframework.todo_app.Ctx
import se.moshicon.klerkframework.todo_app.Data

// JWT configuration constants
private const val JWT_SECRET = "your-secret-key" // In a real app, this would be in a secure config
private const val JWT_ISSUER = "todo-app"
private const val JWT_AUDIENCE = "todo-app-users"

fun Application.configureRouting(klerk: Klerk<Ctx, Data>) {
    // Configure JWT authentication
    install(Authentication) {
        jwt("auth-jwt") {
            realm = "Todo App"
            verifier(
                JWT.require(Algorithm.HMAC256(JWT_SECRET))
                    .build()
            )
            validate { credential ->
                // Accept any valid JWT token
                JWTPrincipal(credential.payload)
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }

    suspend fun contextFromCall(call: ApplicationCall): Ctx = call.context(klerk)
    val lowCodeConfig = LowCodeConfig(
        basePath = "/admin",
        contextProvider = ::contextFromCall,
        showOptionalParameters = { false },
        cssPath = "https://unpkg.com/almond.css@latest/dist/almond.min.css",
    )

    routing {
        route("/api") {
            // Public routes (no authentication required)
            route("/users") {
                apply(registerUsersRoutes(klerk))
            }

            // Protected routes (require authentication)
            authenticate("auth-jwt") {
                route("/todos") {
                    apply(registerTodoRoutes(klerk))
                }
            }
        }

        route("/custom") {
            apply(registerFullControlModeRoutes(klerk))
        }

        // The auto-generated Admin UI
        val autoAdminUI = LowCodeMain(klerk, lowCodeConfig)
        apply(autoAdminUI.registerRoutes())
    }
}

/**
 * Creates a Context from a Call.
 * Extracts user information from JWT token if present.
 */
fun ApplicationCall.context(klerk: Klerk<Ctx, Data>): Ctx {
    val principal = this.principal<JWTPrincipal>()

    return if (principal != null) {
        try {
            // Extract user information from JWT claims
            val username = principal.payload.getClaim("sub").asString()

            // Extract groups from JWT claims (if present)
            val groups = try {
                principal.payload.getClaim("groups").asList(String::class.java) ?: listOf()
            } catch (e: Exception) {
                listOf<String>()
            }

            // Create context with user identity
            val externalId = 42L
            Ctx(CustomIdentity(id = null, externalId = externalId))
        } catch (e: Exception) {
            // Fallback to system identity if JWT parsing fails
            println("Error parsing JWT: ${e.message}")
            Ctx(SystemIdentity)
        }
    } else {
        // No JWT token, use system identity
        Ctx(SystemIdentity)
    }
}
