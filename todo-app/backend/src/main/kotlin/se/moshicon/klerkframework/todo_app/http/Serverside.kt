package se.moshicon.klerkframework.todo_app.http

import dev.klerkframework.klerk.EventWithParameters
import dev.klerkframework.klerk.Klerk
import dev.klerkframework.klerk.misc.EventParameters
import dev.klerkframework.web.EventFormTemplate
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.body
import se.moshicon.klerkframework.todo_app.Ctx
import se.moshicon.klerkframework.todo_app.Data
import se.moshicon.klerkframework.todo_app.notes.CreateTodo
import se.moshicon.klerkframework.todo_app.notes.CreateTodoParams


object FormTemplates {
    lateinit var createTodoTemplate: EventFormTemplate<CreateTodoParams, Ctx>
        private set

    fun init(klerk: Klerk<Ctx, Data>) {
        createTodoTemplate = EventFormTemplate(
            EventWithParameters(
                CreateTodo.id,
                EventParameters(CreateTodoParams::class)
            ),
            klerk, "create",
        ) {
            text(CreateTodoParams::title)
            text(CreateTodoParams::description)
        }
    }
}

fun registerServersideRoutes(klerk: Klerk<Ctx, Data>): Route.() -> Unit = {
    FormTemplates.init(klerk)

    get("/{...}") { indexPage(call, klerk) }
}

suspend fun indexPage(call: ApplicationCall, klerk: Klerk<Ctx, Data>) {
    val context = call.context(klerk)
    val createTodoForm = klerk.read(context) {
        FormTemplates.createTodoTemplate.build(call, null, this, translator = context.translator)
    }

    call.respondHtml {
        body {
            createTodoForm.render(this)
        }
    }
}

