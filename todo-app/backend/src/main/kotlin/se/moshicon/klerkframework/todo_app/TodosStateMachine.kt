package se.moshicon.klerkframework.todo_app

import dev.klerkframework.klerk.ArgForInstanceNonEvent
import dev.klerkframework.klerk.ArgForVoidEvent
import dev.klerkframework.klerk.InstanceEventNoParameters
import dev.klerkframework.klerk.VoidEventWithParameters
import dev.klerkframework.klerk.statemachine.stateMachine
import kotlinx.datetime.Instant
import se.moshicon.klerkframework.todo_app.TodoStates.*
import java.util.*
import kotlin.time.Duration.Companion.minutes

enum class TodoStates {
    Created,
    Completed,
    Trashed,
}

val todoStateMachine = stateMachine {
    event(CreateTodo) {
    }
    event(MarkComplete) {
    }
    event(UnmarkComplete) {
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
        onEvent(MarkComplete) {
            transitionTo(Completed)
        }
    }

    state(Completed) {
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

object CreateTodo : VoidEventWithParameters<Todo, CreateTodoParams>(Todo::class, true, CreateTodoParams::class)
class CreateTodoParams(val title: TodoTitle, val description: TodoDescription)
fun createTodo(args: ArgForVoidEvent<Todo, CreateTodoParams, Ctx, Data>): Todo {
    return Todo(
        todoID = TodoID(UUID.randomUUID().toString()),
        title = args.command.params.title,
        description = args.command.params.description,
//        completed = TodoCompletedStatus(false),
//        priority = TodoPriority(2),
    )
}

object MarkComplete : InstanceEventNoParameters<Todo>(Todo::class, true)
object UnmarkComplete : InstanceEventNoParameters<Todo>(Todo::class, true)
object MoveToTrash : InstanceEventNoParameters<Todo>(Todo::class, true)

fun autoDeleteTodoInTrashTime(args: ArgForInstanceNonEvent<Todo, Ctx, Data>): Instant {
    //return args.time.plus(30.days)
    return args.time.plus(1.minutes)
}