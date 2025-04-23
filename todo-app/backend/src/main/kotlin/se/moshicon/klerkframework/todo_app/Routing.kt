package se.moshicon.klerkframework.todo_app

import dev.klerkframework.klerk.EventReference
import dev.klerkframework.klerk.Klerk
import dev.klerkframework.klerk.SystemIdentity
import dev.klerkframework.web.LowCodeConfig
import dev.klerkframework.web.LowCodeMain

import io.ktor.server.application.*
import io.ktor.server.routing.*
import se.moshicon.klerkframework.todo_app.httpapi.getTodos

fun Application.configureRouting(klerk: Klerk<Ctx, Data>) {
    suspend fun contextFromCall(call: ApplicationCall): Ctx = call.context(klerk)
    val lowCodeConfig = LowCodeConfig(
        basePath = "/admin",
        contextProvider = ::contextFromCall,
        showOptionalParameters = ::showOptionalParameters,
        cssPath = "https://unpkg.com/almond.css@latest/dist/almond.min.css",
//        knownAlgorithms = setOf(IsAutomaticDraw)
    )

    routing {
//        get("/") { listGames(call, klerk, lowCodeConfig) }
        get("/todos") { getTodos(call, klerk) }
//        post("/game/{id}") { confirmMove(call, klerk) }
//        get("/sse/{id}") { handleSse(call, klerk) }

        // The auto-generated Admin UI
        val autoAdminUI = LowCodeMain(klerk, lowCodeConfig)
        apply(autoAdminUI.registerRoutes())
    }
}


internal fun showOptionalParameters(event: EventReference) = false


//suspend fun ApplicationCall.context(klerk: Klerk<Ctx, Data>): Ctx {
//    return Ctx(SystemIdentity)
//}
/**
 * Creates a Context from a Call.
 * As authentication is something that should not be handled by Klerk, we will just fake it here.
 */
suspend fun ApplicationCall.context(klerk: Klerk<Ctx, Data>): Ctx {
//    return Ctx(val user = klerk.read(Ctx(SystemIdentity)) {
//        getFirstWhere(data.users.all) { it.props.name.valueWithoutAuthorization == "Alice" }
//    }
    return Ctx(SystemIdentity)
}

///**
// * Creates a Context from a GraphQLContext. Used in the GraphQL API.
// *
// * In a real app we would use a session token or similar to figure out who the user is. Here, we always just use the
// * user Alice.
// */
//suspend fun GraphQLContext.context(klerk: Klerk<Ctx, Collections>): Ctx {
//    val user = klerk.read(Ctx.system()) {
//        getFirstWhere(data.users.all) { it.props.name.valueWithoutAuthorization == "Alice" }
//    }
//    return Ctx.fromUser(user)
//}
