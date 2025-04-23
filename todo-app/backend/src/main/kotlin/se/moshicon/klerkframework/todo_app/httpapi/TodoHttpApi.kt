package se.moshicon.klerkframework.todo_app.httpapi

import dev.klerkframework.klerk.Klerk
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import se.moshicon.klerkframework.todo_app.*

@Serializable
data class TodoResponse(
    val todoID: String,
    val title: String,
    val description: String,
    val state: String,
)

@Serializable
data class CreateTodoParams(val title: String, val description: String)

suspend fun getTodos(call: ApplicationCall, klerk: Klerk<Ctx, Data>) {
    val context = call.context(klerk)
    val todos = klerk.read(context) {
        // Convert domain Todo objects to TodoResponse objects
        list(data.todos.all).map { todo ->
            TodoResponse(
                todoID = todo.props.todoID.value,
                title = todo.props.title.value,
                description = todo.props.description.value,
                state = todo.state
            )
        }
    }
    call.respond(todos)
}