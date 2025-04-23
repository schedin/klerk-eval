package se.moshicon.klerkframework.todo_app.httpapi

import dev.klerkframework.klerk.Klerk
import io.ktor.server.application.*
import io.ktor.server.response.*
import se.moshicon.klerkframework.todo_app.Ctx
import se.moshicon.klerkframework.todo_app.Data
import se.moshicon.klerkframework.todo_app.context

suspend fun getTodos(call: ApplicationCall, klerk: Klerk<Ctx, Data>) {
    val context = call.context(klerk)
    val todos =klerk.read(context) {
        data.todos.all
    }

    println(todos)
    call.respond("test")
}