package se.moshicon.klerkframework.todo_app.httpapi

import dev.klerkframework.klerk.EventWithParameters
import dev.klerkframework.klerk.Klerk
import dev.klerkframework.web.EventFormTemplate
import io.ktor.server.application.*
import io.ktor.server.response.*
import dev.klerkframework.klerk.misc.EventParameters
import dev.klerkframework.web.ParseResult
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.body
import se.moshicon.klerkframework.todo_app.*
import io.ktor.server.routing.get
import io.ktor.server.routing.post

/**
 * Singleton object to hold the createTodoTemplate instance
 * This ensures the template is only created once and can be reused
 */
object TodoFormTemplate {
    private var _createTodoTemplate: EventFormTemplate<CreateTodoParams, Ctx>? = null

    /**
     * Get or initialize the template with the Klerk instance
     */
    fun createTodoTemplate(klerk: Klerk<Ctx, Data>): EventFormTemplate<CreateTodoParams, Ctx> {
        if (_createTodoTemplate == null) {
            _createTodoTemplate = EventFormTemplate(
                EventWithParameters(
                    CreateTodo.id,
                    EventParameters(CreateTodoParams::class)
                ), klerk, "create",
            ) {
                text(CreateTodoParams::title)
                text(CreateTodoParams::description)
            }
        }
        return _createTodoTemplate!!
    }
}

fun registerCustomRoutes(klerk: Klerk<Ctx, Data>): Route.() -> Unit = {
    // Initialize the template when routes are registered
    TodoFormTemplate.createTodoTemplate(klerk)

    get("/createForm") {
        showCreateTodoForm(call, klerk)
    }
    post("/create") {
        handleCreateTodoFormPost(call, klerk)
    }
}

suspend fun handleCreateTodoFormPost(call: ApplicationCall, klerk: Klerk<Ctx, Data>) {
    when (val result = TodoFormTemplate.createTodoTemplate(klerk).parse(call)) {
        is ParseResult.Invalid -> EventFormTemplate.respondInvalid(result, call)
        is ParseResult.DryRun -> println("TODO: describe what to do here")
        is ParseResult.Parsed -> println(result.params)
    }
    call.respond("TODO create not implemented")
}

suspend fun showCreateTodoForm(call: ApplicationCall, klerk: Klerk<Ctx, Data>) {
    val template = TodoFormTemplate.createTodoTemplate(klerk)
    val context = call.context(klerk)
    val createTodoForm = klerk.read(context) {
        template.build(call, null, this, translator = context.translator)
    }

    call.respondHtml {
        body {
            createTodoForm.render(this)
        }
    }
}






