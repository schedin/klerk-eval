package se.moshicon.klerkframework.todo_app

import dev.klerkframework.klerk.Klerk
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.application.*
import io.ktor.serialization.kotlinx.json.*

import kotlinx.coroutines.runBlocking

fun main() {
    val klerk = Klerk.create(createConfig())
    runBlocking {
        klerk.meta.start()
//        if (klerk.meta.modelsCount == 0) {
//            createInitialTodo(klerk)
//        }
//        //Find the initial todo and print it
//        val todo = klerk.read(Ctx(SystemIdentity)) {
//            getFirstWhere(data.todos.all) { it.props.title == TodoTitle("Todo 1") }
//        }
//
//        println(todo)
//        moveTodoToTrash(klerk, todo)

    }
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = {
        install(ContentNegotiation) {
            json()
        }
        configureRouting(klerk)
    }).start(wait = true)
    //Thread.sleep(10.minutes.inWholeMilliseconds)
}

//suspend fun moveTodoToTrash(klerk: Klerk<Ctx, Data>, todo: Model<Todo>) {
//    val command = Command(
//        event = MoveToTrash,
//        model = todo.id,
//        params = null
//    )
//    klerk.handle(command, Ctx(SystemIdentity), ProcessingOptions(CommandToken.simple()))
//}
//
//suspend fun createInitialTodo(klerk: Klerk<Ctx, Data>) {
//    val command = Command(
//        event = CreateTodo,
//        model = null,
//        params = CreateTodoParams(
//            title = TodoTitle("Todo 1"),
//            description = TodoDescription("This is the first todo")
//        ),
//    )
//    klerk.handle(command, Ctx(SystemIdentity), ProcessingOptions(CommandToken.simple()))
//}
