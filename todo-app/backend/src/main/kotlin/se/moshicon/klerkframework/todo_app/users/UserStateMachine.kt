package se.moshicon.klerkframework.todo_app.users

import dev.klerkframework.klerk.*
import dev.klerkframework.klerk.command.Command
import dev.klerkframework.klerk.statemachine.stateMachine
import se.moshicon.klerkframework.todo_app.Ctx
import se.moshicon.klerkframework.todo_app.Data
import se.moshicon.klerkframework.todo_app.notes.DeleteTodoInternal
import se.moshicon.klerkframework.todo_app.notes.DeleteTodoInternalParams
import se.moshicon.klerkframework.todo_app.notes.Todo

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



//ArgForInstanceEvent<User, Nothing?, Ctx, Data>
fun deleteAllTodosForUser(args: ArgForInstanceEvent<User, Nothing?, Ctx, Data>): List<Command<out Any, out Any>> {
    val userId = args.model.id
    val allUserTodos = args.reader.list(args.reader.data.todos.all) {
        it.props.userID == userId
    }

    allUserTodos.forEach {
        println(it)
    }

    return allUserTodos.map { todoModel ->
        Command(
            event = DeleteTodoInternal,
            model = todoModel.id,
            params = DeleteTodoInternalParams(),
        )
    }
//    return emptyList()
}



object CreateUser : VoidEventWithParameters<User, CreateUserParams>(User::class, true, CreateUserParams::class)
object DeleteUser : InstanceEventNoParameters<User>(User::class, true)

fun createUser(args: ArgForVoidEvent<User, CreateUserParams, Ctx, Data>): User {
    return User(name = args.command.params.name)
}
