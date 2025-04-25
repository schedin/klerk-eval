package se.moshicon.klerkframework.todo_app

import dev.klerkframework.klerk.Klerk
import dev.klerkframework.klerk.SystemIdentity
import dev.klerkframework.web.LowCodeConfig
import dev.klerkframework.web.LowCodeMain

import io.ktor.server.application.*
import io.ktor.server.routing.*
import se.moshicon.klerkframework.todo_app.httpapi.*

fun Application.configureRouting(klerk: Klerk<Ctx, Data>) {
    suspend fun contextFromCall(call: ApplicationCall): Ctx = call.context(klerk)
    val lowCodeConfig = LowCodeConfig(
        basePath = "/admin",
        contextProvider = ::contextFromCall,
        showOptionalParameters = { false },
        cssPath = "https://unpkg.com/almond.css@latest/dist/almond.min.css",
    )

    routing {
        route("/api") {
            get("/todos") {
                getTodos(call, klerk)
            }
            post("/todos") {
                createTodo(call, klerk)
            }
            post("/todos/{todoID}/trash") {
                trash(call, klerk)
            }
            post("/todos/{todoID}/untrash") {
                unTrash(call, klerk)
            }
            post("/todos/{todoID}/complete") {
                markComplete(call, klerk)
            }
            post("/todos/{todoID}/uncomplete") {
                markUncomplete(call, klerk)
            }
            delete("/todos/{todoID}") {
                delete(call, klerk)
            }
        }

        // The auto-generated Admin UI
        val autoAdminUI = LowCodeMain(klerk, lowCodeConfig)
        apply(autoAdminUI.registerRoutes())
    }
}

/**
 * Creates a Context from a Call.
 * As authentication is something that should not be handled by Klerk, we will just fake it here.
 */
fun ApplicationCall.context(klerk: Klerk<Ctx, Data>): Ctx {
    return Ctx(SystemIdentity)
}
