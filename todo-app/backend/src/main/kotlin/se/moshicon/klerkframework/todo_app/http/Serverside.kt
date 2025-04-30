package se.moshicon.klerkframework.todo_app.http

import dev.klerkframework.klerk.CommandResult
import dev.klerkframework.klerk.EventWithParameters
import dev.klerkframework.klerk.Klerk
import dev.klerkframework.klerk.command.Command
import dev.klerkframework.klerk.command.ProcessingOptions
import dev.klerkframework.klerk.misc.EventParameters
import dev.klerkframework.web.EventFormTemplate
import dev.klerkframework.web.ParseResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.body
import se.moshicon.klerkframework.todo_app.Ctx
import se.moshicon.klerkframework.todo_app.Data
import se.moshicon.klerkframework.todo_app.notes.CreateTodo
import se.moshicon.klerkframework.todo_app.notes.CreateTodoParams
import se.moshicon.klerkframework.todo_app.notes.TodoDescription
import se.moshicon.klerkframework.todo_app.notes.TodoTitle
import se.moshicon.klerkframework.todo_app.users.UserName


object FormTemplates {
    lateinit var createTodoTemplate: EventFormTemplate<CreateTodoParams, Ctx>
        private set

    fun init(klerk: Klerk<Ctx, Data>) {
        createTodoTemplate = EventFormTemplate(
            EventWithParameters(
                CreateTodo.id,
                EventParameters(CreateTodoParams::class)
            ),
            klerk, "todos",
        ) {
            text(CreateTodoParams::title)
            text(CreateTodoParams::description)
//            populatedAfterSubmit(CreateTodoParams::username)
            text(CreateTodoParams::username)
        }
    }
}

fun registerServersideRoutes(klerk: Klerk<Ctx, Data>): Route.() -> Unit = {
    FormTemplates.init(klerk)
    get("/{...}") { indexPage(call, klerk) }
    post("/todos") {
        handleCreateTodo(call, klerk)
    }
}

suspend fun handleCreateTodo(call: ApplicationCall, klerk: Klerk<Ctx, Data>) {
    when (val result = FormTemplates.createTodoTemplate.parse(call)) {
        is ParseResult.Invalid -> EventFormTemplate.respondInvalid(result, call)
        is ParseResult.DryRun -> println("TODO: describe what to do here")
        is ParseResult.Parsed -> {
            println("ParseResult.Parsed")
            val context = call.context(klerk)
            val command = Command(
                event = CreateTodo,
                model = null,
                params = result.params
            )
            when(val commandResult = klerk.handle(command, context, ProcessingOptions(result.key))) {
                is CommandResult.Failure -> {
                    call.respond(HttpStatusCode.BadRequest, commandResult.problem.toString())
                }
                is CommandResult.Success -> {
                    val createdTodo = klerk.read(context) {
                        get(commandResult.primaryModel!!)
                    }
                    call.respondRedirect("")
                }
            }
        }
    }
}

suspend fun indexPage(call: ApplicationCall, klerk: Klerk<Ctx, Data>) {
    // Set user_info cookie if it doesn't exist
    if (call.request.cookies["user_info"] == null) {
        call.response.cookies.append(
            Cookie(
                name = "user_info",
                value = "Alice:admins,users",
                path = "/",
                httpOnly = false
            )
        )
    }

    val context = call.context(klerk)
    val initialValues = CreateTodoParams(
        title = TodoTitle(""),
        description = TodoDescription(""),
        username = UserName("Alice"),
    )

    val createTodoForm = klerk.read(context) {
        FormTemplates.createTodoTemplate.build(call, initialValues, this, translator = context.translator)
    }

    call.respondHtml {
        body {
            createTodoForm.render(this)
        }
    }
}
