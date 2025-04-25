package se.moshicon.klerkframework.todo_app.http

import dev.klerkframework.klerk.Klerk
import dev.klerkframework.klerk.Model

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import se.moshicon.klerkframework.todo_app.*
import se.moshicon.klerkframework.todo_app.users.User

fun registerUsersRoutes(klerk: Klerk<Ctx, Data>): Route.() -> Unit = {
    get("/{...}") { getUsers(call, klerk) }
}

@Serializable
data class UserResponse(
    val username: String,
)

fun toUserResponse(user: Model<User>) = UserResponse(
    username = user.props.name.value,
)

suspend fun getUsers(call: ApplicationCall, klerk: Klerk<Ctx, Data>) {
    val context = call.context(klerk)
    val users = klerk.read(context) {
        list(data.users.all).map { user -> toUserResponse(user) }
    }
    call.respond(users)
}
