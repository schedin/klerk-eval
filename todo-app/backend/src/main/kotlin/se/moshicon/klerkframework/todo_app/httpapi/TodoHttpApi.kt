package se.moshicon.klerkframework.todo_app.httpapi

import dev.klerkframework.klerk.Klerk
import dev.klerkframework.klerk.Model
import dev.klerkframework.klerk.command.Command
import dev.klerkframework.klerk.command.CommandToken
import dev.klerkframework.klerk.command.ProcessingOptions
import dev.klerkframework.klerk.CommandResult.Success
import dev.klerkframework.klerk.CommandResult.Failure
import dev.klerkframework.klerk.InstanceEventNoParameters

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import se.moshicon.klerkframework.todo_app.*

@Serializable
data class TodoResponse(
    val todoID: String,
    val title: String,
    val description: String,
    val state: String,
    val createdAt: Instant,
)

@Serializable
data class CreateTodoRequest(val title: String, val description: String)

fun toTodoResponse(todo: Model<Todo>) = TodoResponse(
    todoID = todo.id.toString(),
    title = todo.props.title.value,
    description = todo.props.description.value,
    state = todo.state,
    createdAt = todo.createdAt,
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
            call.respond(HttpStatusCode.BadRequest, result.problem.toString())
        }
        is Success -> {
            val createdTodo = klerk.read(context) {
                get(result.primaryModel!!)
            }
            call.respond(HttpStatusCode.Created, toTodoResponse(createdTodo))
        }
    }
}

suspend fun handleTodoCommand(
    call: ApplicationCall,
    klerk: Klerk<Ctx, Data>,
    event: InstanceEventNoParameters<Todo>,
    onSuccess: suspend (s: Model<Todo>) -> Unit = {},
) {
    val todoID = call.parameters["todoID"] ?: throw IllegalArgumentException("todoID is required")
    val context = call.context(klerk)
    val todo = klerk.read(context) {
        getFirstWhere(data.todos.all) { it.id.toString() == todoID }
    }
    val command = Command(
        event = event,
        model = todo.id,
        params = null
    )
    when(val result = klerk.handle(command, context, ProcessingOptions(CommandToken.simple()))) {
        is Failure -> call.respond(HttpStatusCode.BadRequest, result.problem.toString())
        is Success -> {
            val modifiedTodo = klerk.read(context) {
                get(result.primaryModel!!)
            }
            onSuccess(modifiedTodo)
        }
    }
}

suspend fun markComplete(call: ApplicationCall, klerk: Klerk<Ctx, Data>) {
    handleTodoCommand(call, klerk, MarkComplete) {
        call.respond(HttpStatusCode.Created, toTodoResponse(it))
    }
}

suspend fun markUncomplete(call: ApplicationCall, klerk: Klerk<Ctx, Data>) {
    handleTodoCommand(call, klerk, UnmarkComplete) {
        call.respond(HttpStatusCode.Created, toTodoResponse(it))
    }
}

suspend fun delete(call: ApplicationCall, klerk: Klerk<Ctx, Data>) {
    handleTodoCommand(call, klerk, DeleteFromTrash) {
        call.respond(HttpStatusCode.NoContent)
    }
}

suspend fun trash(call: ApplicationCall, klerk: Klerk<Ctx, Data>) {
    handleTodoCommand(call, klerk, MoveToTrash) {
        call.respond(HttpStatusCode.OK, toTodoResponse(it))
    }
}

suspend fun unTrash(call: ApplicationCall, klerk: Klerk<Ctx, Data>) {
    handleTodoCommand(call, klerk, RecoverFromTrash) {
        call.respond(HttpStatusCode.OK, toTodoResponse(it))
    }
}
