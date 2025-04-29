package se.moshicon.klerkframework.todo_app

import dev.klerkframework.klerk.AuthenticationIdentity
import dev.klerkframework.klerk.Klerk
import dev.klerkframework.klerk.SystemIdentity
import dev.klerkframework.klerk.command.Command
import dev.klerkframework.klerk.command.CommandToken
import dev.klerkframework.klerk.command.ProcessingOptions
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.http.*

import kotlinx.coroutines.runBlocking
import se.moshicon.klerkframework.todo_app.http.configureHttpRouting
import se.moshicon.klerkframework.todo_app.users.CreateUser
import se.moshicon.klerkframework.todo_app.users.CreateUserParams
import se.moshicon.klerkframework.todo_app.users.DeleteUser
import se.moshicon.klerkframework.todo_app.users.UserName

fun main() {
    val klerk = Klerk.create(createConfig())
    runBlocking {
        klerk.meta.start()
        createInitialUsers(klerk)
        //deleteUser(klerk,"Bob")
        //deleteUser(klerk,"Alice")
    }
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = {
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
    }).start(wait = true)
}

suspend fun createInitialUsers(klerk: Klerk<Ctx, Data>) {
    val users = klerk.read(Ctx(AuthenticationIdentity)) {
        list(data.users.all)
    }
    if (users.isEmpty()) {
        suspend fun createUser(username: String) {
            val command = Command(
                event = CreateUser,
                model = null,
                params = CreateUserParams(UserName(username)),
            )
            klerk.handle(command, Ctx(AuthenticationIdentity), ProcessingOptions(CommandToken.simple()))
        }
        createUser("Alice")
        createUser("Bob")
        createUser("Charlie")
    }
}

suspend fun deleteUser(klerk: Klerk<Ctx, Data>, username: String) {
    val userToDelete = klerk.read(Ctx(SystemIdentity)) {
        firstOrNull(data.users.all) { it.props.name.value == username }
    }
    if (userToDelete != null) {
        val command = Command(
            event = DeleteUser,
            model = userToDelete.id,
            params = null,
        )
        klerk.handle(command, Ctx(SystemIdentity), ProcessingOptions(CommandToken.simple()))
    }
}
