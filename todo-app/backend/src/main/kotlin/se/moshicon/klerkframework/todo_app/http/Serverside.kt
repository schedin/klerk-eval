package se.moshicon.klerkframework.todo_app.http

import dev.klerkframework.klerk.Klerk
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import se.moshicon.klerkframework.todo_app.Ctx
import se.moshicon.klerkframework.todo_app.Data


fun registerServersideRoutes(klerk: Klerk<Ctx, Data>): Route.() -> Unit = {
    // Initialize the template when routes are registered
//    TodoFormTemplate.createTodoTemplate(klerk)

    get("/{...}") { indexPage(call, klerk) }
}

suspend fun indexPage(call: ApplicationCall, klerk: Klerk<Ctx, Data>) {
    call.respond("TODO for slash")
}

