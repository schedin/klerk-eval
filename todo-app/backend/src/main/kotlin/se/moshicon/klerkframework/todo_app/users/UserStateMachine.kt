package se.moshicon.klerkframework.todo_app.users

import dev.klerkframework.klerk.*
import dev.klerkframework.klerk.statemachine.StateMachine
import dev.klerkframework.klerk.statemachine.stateMachine
import se.moshicon.klerkframework.todo_app.Ctx
import se.moshicon.klerkframework.todo_app.Data

enum class UserStates {
    Created,
    ToBeDeleted,
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
            //TODO: Also delete all todos created by this user
            delete()
        }
    }
}

object CreateUser : VoidEventWithParameters<User, CreateUserParams>(User::class, true, CreateUserParams::class)
object DeleteUser : InstanceEventNoParameters<User>(User::class, true)

fun createUser(args: ArgForVoidEvent<User, CreateUserParams, Ctx, Data>): User {
    return User(name = args.command.params.name)
}
