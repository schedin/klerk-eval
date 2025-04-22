package se.moshicon.klerkframework.todo_app

import dev.klerkframework.klerk.*
import dev.klerkframework.klerk.collection.ModelCollections
import dev.klerkframework.klerk.command.Command
import dev.klerkframework.klerk.command.CommandToken
import dev.klerkframework.klerk.command.ProcessingOptions
import dev.klerkframework.klerk.datatypes.BooleanContainer
import dev.klerkframework.klerk.datatypes.IntContainer
import dev.klerkframework.klerk.datatypes.StringContainer
import dev.klerkframework.klerk.statemachine.stateMachine
import dev.klerkframework.klerk.storage.Persistence
import dev.klerkframework.klerk.storage.SqlPersistence
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.sqlite.SQLiteDataSource
import kotlinx.coroutines.runBlocking
import java.util.*
import se.moshicon.klerkframework.todo_app.TodoStates.*
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes


class Ctx(
    override val actor: ActorIdentity,
    override val auditExtra: String? = null,
    override val time: Instant = Clock.System.now(),
    override val translator: Translator = DefaultTranslator(),
) : KlerkContext

data class Todo(
    val todoID: TodoID,
    val title: TodoTitle,
    val description: TodoDescription,
    val completed: TodoCompletedStatus,
//    val priority: TodoPriority,
)

class TodoPriority(value: Int) : IntContainer(value) {
    override val min = 0
    override val max = 10
}

class TodoID(value: String) : StringContainer(value) {
    override val minLength = 36
    override val maxLength = 36
    override val maxLines = 1
}

class TodoTitle(value: String) : StringContainer(value) {
    override val minLength = 0
    override val maxLength = 100
    override val maxLines = 1
}

class TodoDescription(value: String) : StringContainer(value) {
    override val minLength = 0
    override val maxLength = 100000
    override val maxLines = Int.MAX_VALUE
}

class TodoCompletedStatus constructor(value: Boolean) : BooleanContainer(value) {
}
//sealed class TodoCompletedStatus protected constructor(value: Boolean) : BooleanContainer(value)
//
////object TodoIsCompleted : TodoCompletedStatus(true)
//object TodoIsNotCompleted : TodoCompletedStatus(false)

enum class TodoStates {
    Created,
    Trashed,
}


object Data {
    val todos = ModelCollections<Todo, Ctx>()
}

val todoStateMachine = stateMachine {
    event(CreateTodo) {
    }
    event(MoveToTrash) {
    }


    voidState {
        onEvent(CreateTodo) {
            createModel(Created, ::createTodo)
        }
    }

    state(Created) {
        onEvent(MoveToTrash) {
            transitionTo(Trashed)
        }
    }
    state(Trashed) {
        atTime(::autoDeleteTodoInTrashTime) {
            delete()
        }
    }
}

fun autoDeleteTodoInTrashTime(args: ArgForInstanceNonEvent<Todo, Ctx, Data>): Instant {
    //return args.time.plus(30.days)
    return args.time.plus(1.minutes)
}

object CreateTodo : VoidEventWithParameters<Todo, CreateTodoParams>(Todo::class, true, CreateTodoParams::class)
class CreateTodoParams(val title: TodoTitle, val description: TodoDescription)
fun createTodo(args: ArgForVoidEvent<Todo, CreateTodoParams, Ctx, Data>): Todo {
    return Todo(
        todoID = TodoID(UUID.randomUUID().toString()),
        title = args.command.params.title,
        description = args.command.params.description,
        completed = TodoCompletedStatus(false),
//        priority = TodoPriority(2),
    )
}

object MoveToTrash : InstanceEventNoParameters<Todo>(Todo::class, true)

private fun createPersistence(): Persistence {
    val dbFilePath =
        requireNotNull(System.getenv("DATABASE_PATH")) { "The environment variable 'DATABASE_PATH' must be set" }
    val ds =  SQLiteDataSource()
    ds.url = "jdbc:sqlite:$dbFilePath"
    return SqlPersistence(ds)
}

fun main() {
    val config = ConfigBuilder<Ctx, Data>(Data).build {
        authorization {
            apply(insecureAllowEverything())
        }
        managedModels {
            model(Todo::class, todoStateMachine, Data.todos)
        }
        persistence(createPersistence())
        migrations(setOf(
//            MyMigrationStep1to2
        ))
    }

    val klerk = Klerk.create(config)
    runBlocking {
        klerk.meta.start()
        if (klerk.meta.modelsCount == 0) {
            createInitialTodo(klerk)
        }

//        //Find the initial todo and print it
//        val todo = klerk.read(Ctx(SystemIdentity)) {
//            getFirstWhere(data.todos.all) { it.props.title == TodoTitle("Todo 1") }
//        }
//
//        println(todo)
//        moveTodoToTrash(klerk, todo)

    }
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = {
        configureRouting(klerk)
    }).start(wait = true)
    //Thread.sleep(10.minutes.inWholeMilliseconds)
}

suspend fun moveTodoToTrash(klerk: Klerk<Ctx, Data>, todo: Model<Todo>) {
    val command = Command(
        event = MoveToTrash,
        model = todo.id,
        params = null
    )
    klerk.handle(command, Ctx(SystemIdentity), ProcessingOptions(CommandToken.simple()))
}

suspend fun createInitialTodo(klerk: Klerk<Ctx, Data>) {
    val command = Command(
        event = CreateTodo,
        model = null,
        params = CreateTodoParams(
            title = TodoTitle("Todo 1"),
            description = TodoDescription("This is the first todo")
        ),
    )
    klerk.handle(command, Ctx(SystemIdentity), ProcessingOptions(CommandToken.simple()))
}
