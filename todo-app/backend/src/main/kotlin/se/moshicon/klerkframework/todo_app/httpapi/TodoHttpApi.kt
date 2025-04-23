package se.moshicon.klerkframework.todo_app.httpapi

import dev.klerkframework.klerk.Klerk
import dev.klerkframework.klerk.command.Command
import dev.klerkframework.klerk.command.CommandToken
import dev.klerkframework.klerk.command.ProcessingOptions
import dev.klerkframework.klerk.CommandResult
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

suspend fun createTodo(call: ApplicationCall, klerk: Klerk<Ctx, Data>) {
    val context = call.context(klerk)
    val params = call.receive<CreateTodoRequest>()
    println(params)

    val command = Command(
        event = CreateTodo,
        model = null,
        params = CreateTodoParams(
            title = TodoTitle(params.title),
            description = TodoDescription(params.description),
        ),
    )
    val result = klerk.handle(command, context, ProcessingOptions(CommandToken.simple()))
    if (result is dev.klerkframework.klerk.CommandResult.Failure) {
        call.respond(HttpStatusCode.InternalServerError, result.problem)
        return
    }

    if (result is dev.klerkframework.klerk.CommandResult.Success) {
        println("Success")
        println(result.authorizedModels)
        call.respond(HttpStatusCode.Created) //Not implemneted return value

//        // Get the created todo's ID from the result
//        val modelId = result.authorizedModels.firstOrNull()
//
//        if (modelId != null) {
//            // Get the created todo and return it
//            val createdTodo = klerk.read(context) {
//                getById(data.todos, modelId)
//            }
//
//            // Convert to response object
//            val todoResponse = TodoResponse(
//                todoID = createdTodo.props.todoID.value,
//                title = createdTodo.props.title.value,
//                description = createdTodo.props.description.value,
//                state = createdTodo.state
//            )
//
//            // Respond with the created todo
//            call.respond(HttpStatusCode.Created, todoResponse)
//        } else {
//            // No model ID found in the result
//            call.respond(HttpStatusCode.Created, "Todo created successfully")
//        }
    }
}