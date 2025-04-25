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

class CustomForm {
    fun initCreateTodoTemplate(klerk: Klerk<Ctx, Data>): EventFormTemplate<CreateTodoParams, Ctx> {
        val createTodoTemplate = EventFormTemplate(
            EventWithParameters(
                CreateTodo.id,
                EventParameters(CreateTodoParams::class)
            ), klerk, "create",
        ) {
            text(CreateTodoParams::title)
            text(CreateTodoParams::description)
        }
        return createTodoTemplate
    }
    companion object {
    }
}

fun registerCustomRoutes(klerk: Klerk<Ctx, Data>): Route.() -> Unit = {


    get("/createForm") {
        showCreateTodoForm(call, klerk)
    }
    post("/create") {
        handleCreateTodoFormPost(call, klerk)
    }
}

suspend fun handleCreateTodoFormPost(call: ApplicationCall, klerk: Klerk<Ctx, Data>) {
    call.respond("TODO create not implemented")
    when (val result = createTodoTemplate.parse(call)) {
        is ParseResult.Invalid -> EventFormTemplate.respondInvalid(result, call)
        is ParseResult.DryRun -> println("TODO: describe what to do here")
        is ParseResult.Parsed -> println(result.params)
    }
}

suspend fun showCreateTodoForm(call: ApplicationCall, klerk: Klerk<Ctx, Data>) {
    val template = createTodoTemplate(klerk)
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






