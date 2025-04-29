package se.moshicon.klerkframework.todo_app.notes

import dev.klerkframework.klerk.ArgForInstanceNonEvent
import dev.klerkframework.klerk.ArgForVoidEvent
import dev.klerkframework.klerk.InstanceEventNoParameters
import dev.klerkframework.klerk.VoidEventWithParameters
import dev.klerkframework.klerk.statemachine.stateMachine
import kotlinx.datetime.Instant
import se.moshicon.klerkframework.todo_app.Ctx
import se.moshicon.klerkframework.todo_app.Data
import se.moshicon.klerkframework.todo_app.notes.TodoStates.*
import se.moshicon.klerkframework.todo_app.users.GroupModelIdentity
import se.moshicon.klerkframework.todo_app.users.User
import kotlin.time.Duration.Companion.days


enum class TodoStates {
    Created,
    Completed,
    Trashed,
}

val todoStateMachine = stateMachine {
    event(CreateTodo) { }
    event(MarkComplete) { }
    event(UnmarkComplete) { }
    event(MoveToTrash) { }
    event(RecoverFromTrash) { }
    event(DeleteFromTrash) { }

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
        onEvent(UnmarkComplete) {
            transitionTo(Created)
        }
    }

    state(Trashed) {
        onEvent(RecoverFromTrash) {
            transitionTo(Created)
        }
        atTime(::autoDeleteTodoInTrashTime) {
            delete()
        }
        onEvent(DeleteFromTrash) {
            delete()
        }
    }
}

object CreateTodo : VoidEventWithParameters<Todo, CreateTodoParams>(Todo::class, true, CreateTodoParams::class)
class CreateTodoParams(val title: TodoTitle, val description: TodoDescription, val user: User)
fun createTodo(args: ArgForVoidEvent<Todo, CreateTodoParams, Ctx, Data>): Todo {
    val actor = args.context.actor
    if (actor !is GroupModelIdentity) {
        throw IllegalStateException("Actor must be a GroupModelIdentity")
    }
    return Todo(
        title = args.command.params.title,
        description = args.command.params.description,
        userID = actor.id,
    )
}

object MarkComplete : InstanceEventNoParameters<Todo>(Todo::class, true)
object UnmarkComplete : InstanceEventNoParameters<Todo>(Todo::class, true)
object MoveToTrash : InstanceEventNoParameters<Todo>(Todo::class, true)
object RecoverFromTrash : InstanceEventNoParameters<Todo>(Todo::class, true)
object DeleteFromTrash : InstanceEventNoParameters<Todo>(Todo::class, true)

fun autoDeleteTodoInTrashTime(args: ArgForInstanceNonEvent<Todo, Ctx, Data>): Instant {
    return args.time.plus(1.days)
    //return args.time.plus(1.minutes)
}