package se.moshicon.klerkframework.todo_app

import dev.klerkframework.klerk.*
import se.moshicon.klerkframework.todo_app.notes.CreateTodo
import se.moshicon.klerkframework.todo_app.notes.CreateTodoParams
import se.moshicon.klerkframework.todo_app.notes.Todo
import se.moshicon.klerkframework.todo_app.users.GroupModelIdentity
import se.moshicon.klerkframework.todo_app.users.User
import dev.klerkframework.klerk.PositiveAuthorization.*


fun authorizationRules(): ConfigBuilder.AuthorizationRulesBlock<Ctx, Data>.() -> Unit = {
    commands {
        positive {
            rule(::userCanCreateOwnTodos)
            rule(::userCanModifyOwnTodos)
        }
        negative {
        }
    }
    readModels {
        positive {
            rule(::unAuthenticatedCanReadUsers)
            rule(::authenticationIdentityCanReadUsers)
            rule(::userCanReadOwnTodos)
        }
        negative {
        }
    }
    readProperties {
        positive {
            rule(::authenticationIdentityCanReadUsersProps)
            rule(::userCanReadOwnTodoProps)
            rule(::userCanReadOwnUserProps)
            rule(::unAuthenticatedCanReadUsersProps)
        }
        negative {
        }
    }
    eventLog {
        positive {
            rule(::allCanReadEventLog)
        }
        negative {
        }
    }
}

fun allCanReadEventLog(@Suppress("UNUSED_PARAMETER") args: ArgContextReader<Ctx, Data>): PositiveAuthorization {
    return Allow
}

fun userCanReadOwnUserProps(args: ArgsForPropertyAuth<Ctx, Data>): PositiveAuthorization {
    val actor = args.context.actor
    val user = args.model.props
    if (actor is GroupModelIdentity && user is User && user == actor.model.props) {
        return Allow
    }
    return NoOpinion
}

fun userCanReadOwnTodoProps(args: ArgsForPropertyAuth<Ctx, Data>): PositiveAuthorization {
    val actor = args.context.actor
    val todo = args.model.props
    if (actor is GroupModelIdentity && todo is Todo && todo.user == actor.model.props) {
        return Allow
    }
    return NoOpinion
}

fun userCanCreateOwnTodos(args: ArgCommandContextReader<*, Ctx, Data>): PositiveAuthorization {
    val actor = args.context.actor
    val createParams = args.command.params

    if (actor is GroupModelIdentity && args.command.event is CreateTodo &&
        createParams is CreateTodoParams &&
        createParams.user == actor.model.props
    ) {
        return Allow
    }
    return NoOpinion
}

fun userCanModifyOwnTodos(args: ArgCommandContextReader<*, Ctx, Data>): PositiveAuthorization {
    val actor = args.context.actor
    val commandModel = args.command.model
    println(commandModel)
    if (actor is GroupModelIdentity) {
        val loggedInAsUser = actor.model.props.name
        println(loggedInAsUser)

        if (commandModel is ModelID<*>)
            val todoModel = args.reader.get <Todo> ( commandModel)
        }

        return Allow
    }
    return NoOpinion
}

fun unAuthenticatedCanReadUsers(args: ArgModelContextReader<Ctx, Data>): PositiveAuthorization {
    if (args.context.actor == Unauthenticated && args.model.props is User) {
        return Allow
    }
    return NoOpinion
}

fun unAuthenticatedCanReadUsersProps(args: ArgsForPropertyAuth<Ctx, Data>): PositiveAuthorization {
    val actor = args.context.actor
    val user = args.model.props
    if (actor is Unauthenticated && user is User) {
        return Allow
    }
    return NoOpinion
}

fun authenticationIdentityCanReadUsers(args: ArgModelContextReader<Ctx, Data>): PositiveAuthorization {
    if (args.context.actor == AuthenticationIdentity && args.model.props is User) {
        return Allow
    }
    return NoOpinion
}

fun authenticationIdentityCanReadUsersProps(args: ArgsForPropertyAuth<Ctx, Data>): PositiveAuthorization {
    val actor = args.context.actor
    val user = args.model.props
    if (actor is AuthenticationIdentity && user is User) {
        return Allow
    }
    return NoOpinion
}

fun userCanReadOwnTodos(args: ArgModelContextReader<Ctx, Data>): PositiveAuthorization {
    val actor = args.context.actor
    val todo = args.model.props
    if (actor is GroupModelIdentity && todo is Todo && todo.user == actor.model.props) {
        return Allow
    }
    return NoOpinion
}
