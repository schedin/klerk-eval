package se.moshicon.klerkframework.todo_app.users

import dev.klerkframework.klerk.*
import dev.klerkframework.klerk.command.Command
import dev.klerkframework.klerk.statemachine.stateMachine
import se.moshicon.klerkframework.todo_app.Ctx
import se.moshicon.klerkframework.todo_app.Data
import se.moshicon.klerkframework.todo_app.notes.DeleteTodoInternal

enum class UserStates {
    Created,
}

val userStateMachine = stateMachine {
    event(CreateUser) { }
    event(DeleteUser) { }

    voidState {
        onEvent(CreateUser) {
            createModel(initialState = UserStates.Created, ::createUser)
        }
    }

    state(UserStates.Created) {
        onEvent(DeleteUser) {
            createCommands(::deleteAllTodosForUser)
            delete()
        }
    }
}

fun deleteAllTodosForUser(args: ArgForInstanceEvent<User, Nothing?, Ctx, Data>): List<Command<out Any, out Any>>  {
    val commands = ArrayList<Command<out Any, out Any>>()

    args.reader.apply {
        val userId = args.model.id
        val allUserTodos = list(args.reader.data.todos.all) {
            it.props.userID == userId
        }
        allUserTodos.forEach { todo ->
            commands.add(Command(
                event = DeleteTodoInternal,
                model = todo.id,
                params = null
            ))
        }
    }

    return commands
}


object CreateUser : VoidEventWithParameters<User, CreateUserParams>(User::class, true, CreateUserParams::class)
object DeleteUser : InstanceEventNoParameters<User>(User::class, true)

fun createUser(args: ArgForVoidEvent<User, CreateUserParams, Ctx, Data>): User {
    return User(name = args.command.params.name)
}
