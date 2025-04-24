package se.moshicon.klerkframework.todo_app.httpapi

import dev.klerkframework.klerk.Klerk
import dev.klerkframework.klerk.Model
import dev.klerkframework.klerk.command.Command
import dev.klerkframework.klerk.command.CommandToken
import dev.klerkframework.klerk.command.ProcessingOptions
import dev.klerkframework.klerk.CommandResult.Success
import dev.klerkframework.klerk.CommandResult.Failure

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
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
data class CreateTodoRequest(val title: String, val description: String)

fun toTodoResponse(todo: Model<Todo>) = TodoResponse(
    todoID = todo.props.todoID.value,
    title = todo.props.title.value,
    description = todo.props.description.value,
    state = todo.state
)

suspend fun getTodos(call: ApplicationCall, klerk: Klerk<Ctx, Data>) {
    val context = call.context(klerk)
    val todos = klerk.read(context) {
        list(data.todos.all).map { todo -> toTodoResponse(todo) }
    }
    call.respond(todos)
}

suspend fun createTodo(call: ApplicationCall, klerk: Klerk<Ctx, Data>) {
    val context = call.context(klerk)
    val params = call.receive<CreateTodoRequest>()

    val command = Command(
        event = CreateTodo,
        model = null,
        params = CreateTodoParams(
            title = TodoTitle(params.title),
            description = TodoDescription(params.description),
        ),
    )
    when(val result = klerk.handle(command, context, ProcessingOptions(CommandToken.simple()))) {
        is Failure -> {
            call.respond(HttpStatusCode.InternalServerError, result.problem)
        }
        is Success -> {
            val createdTodo = result.authorizedModels.entries.firstOrNull()?.value as? Model<Todo>
            if (createdTodo == null) {
                call.respond(HttpStatusCode.InternalServerError, "Could not find created todo")
            } else {
                call.respond(HttpStatusCode.Created, toTodoResponse(createdTodo))
            }
        }
    }
}