package se.moshicon.klerkframework.todo_app.httpapi

import dev.klerkframework.klerk.EventWithParameters
import dev.klerkframework.klerk.Klerk
import dev.klerkframework.web.EventFormTemplate
import io.ktor.server.application.*
import io.ktor.server.response.*
import dev.klerkframework.klerk.misc.EventParameters
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.body
import se.moshicon.klerkframework.todo_app.*
import io.ktor.server.routing.get

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

//    when (val result = createAuthorFormTemplate.parse(call)) {
//        is Invalid -> EventFormTemplate.respondInvalid(result, call)
//        is DryRun -> // TODO: describe what to do here
//        is Parsed -> // the parameters are now available in result.params
//    }
}

suspend fun showCreateTodoForm(call: ApplicationCall, klerk: Klerk<Ctx, Data>) {
    val template = initCreateTodoTemplate(klerk)
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



