package se.moshicon.klerkframework.todo_app

import dev.klerkframework.klerk.*
import dev.klerkframework.klerk.collection.ModelCollections
import dev.klerkframework.klerk.statemachine.stateMachine
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.*

class Ctx(
    override val actor: ActorIdentity,
    override val auditExtra: String? = null,
    override val time: Instant = Clock.System.now(),
    override val translator: Translator = DefaultTranslator(),
) : KlerkContext

data class Todo(
    val id: UUID,
    val title: String,
    val description: String,
    val completed: Boolean
)

object Data {
    val todos = ModelCollections<Todo, Ctx>()
}

enum class TodoStates {
    Created
}

val todoStateMachine = stateMachine {
    voidState {
        onEvent(CreateTodo) {
            createModel(TodoStates.Created, ::createTodo)
        }
    }
}

object CreateTodo : VoidEventWithParameters<Todo, CreateTodoParams>(Todo::class, true, CreateTodoParams::class)
class CreateTodoParams(val title: String, val description: String)
fun createTodo(args: ArgForVoidEvent<Todo, CreateTodoParams, Ctx, Data>): Todo {
    return Todo(id = UUID.randomUUID(), title = args.command.params.title, description = args.command.params.description, completed = false)
}


fun main() {
    println("Hello, Klerk Framework")

    val config = ConfigBuilder<Ctx, Data>(Data).build {
        authorization {
            apply(insecureAllowEverything())
        }
        managedModels {
            model(Todo::class, todoStateMachine, Data.todos)
        }
    }

    val klerk = Klerk.create(config)
    println(klerk)
}