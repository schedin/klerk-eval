package se.moshicon.klerkframework.todo_app.http

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import dev.klerkframework.klerk.*
import dev.klerkframework.klerk.command.Command
import dev.klerkframework.klerk.command.CommandToken
import dev.klerkframework.klerk.command.ProcessingOptions
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
import se.moshicon.klerkframework.todo_app.users.CreateUser
import se.moshicon.klerkframework.todo_app.users.CreateUserParams
import se.moshicon.klerkframework.todo_app.users.User
import se.moshicon.klerkframework.todo_app.users.UserName

// JWT configuration constants
// Note: In this demo, we're using a simplified JWT implementation without real verification
// In a real app, you would use proper JWT verification with a secure secret key
private const val JWT_SECRET = "your-secret-key"
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
                // Accept any token that passes our simple verification
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
suspend fun ApplicationCall.context(klerk: Klerk<Ctx, Data>): Ctx {
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
            val user = findOrCreateUser(klerk, username)
            Ctx(ModelReferenceIdentity(modelId = user.id))
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

suspend fun findOrCreateUser(klerk: Klerk<Ctx, Data>, username: String): Model<User> {
    val authContext = Ctx(AuthenticationIdentity)

    // Try to find existing user first and return it if found
    return klerk.read(authContext) {
        firstOrNull(data.users.all) { it.props.name.value == username }
    } ?: run {
        // User not found, create a new one, laying trust in the JWT issuer for what users should exist
        val command = Command(
            event = CreateUser,
            model = null,
            params = CreateUserParams(UserName(username)),
        )
        klerk.handle(command, authContext, ProcessingOptions(CommandToken.simple()))

        // Return the newly created user
        klerk.read(authContext) {
            getFirstWhere(data.users.all) { it.props.name.value == username }
        }
    }
}
