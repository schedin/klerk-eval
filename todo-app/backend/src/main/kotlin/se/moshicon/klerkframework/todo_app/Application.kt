package se.moshicon.klerkframework.todo_app

import dev.klerkframework.klerk.Klerk
import dev.klerkframework.klerk.SystemIdentity
import dev.klerkframework.klerk.command.Command
import dev.klerkframework.klerk.command.CommandToken
import dev.klerkframework.klerk.command.ProcessingOptions
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*

import kotlinx.coroutines.runBlocking
import se.moshicon.klerkframework.todo_app.users.CreateUser
import se.moshicon.klerkframework.todo_app.users.CreateUserParams
import se.moshicon.klerkframework.todo_app.users.UserName

fun main() {
    val klerk = Klerk.create(createConfig())
    runBlocking {
        klerk.meta.start()
        createInitialUsers(klerk)
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

suspend fun createInitialUsers(klerk: Klerk<Ctx, Data>) {
    val users = klerk.read(Ctx(SystemIdentity)) {
        list(data.users.all)
    }
    if (users.isEmpty()) {

        suspend fun createUser(username: String) {
            val command = Command(
                event = CreateUser,
                model = null,
                params = CreateUserParams(UserName(username)),
            )
            klerk.handle(command, Ctx(SystemIdentity), ProcessingOptions(CommandToken.simple()))
        }
        createUser("Alice")
        createUser("Bob")
        createUser("Charlie")
    }
}
