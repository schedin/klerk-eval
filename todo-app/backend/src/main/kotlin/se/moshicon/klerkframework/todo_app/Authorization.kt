package se.moshicon.klerkframework.todo_app

import dev.klerkframework.klerk.*
import se.moshicon.klerkframework.todo_app.notes.CreateTodo
import se.moshicon.klerkframework.todo_app.notes.CreateTodoParams
import se.moshicon.klerkframework.todo_app.notes.Todo
import se.moshicon.klerkframework.todo_app.users.GroupModelIdentity
import se.moshicon.klerkframework.todo_app.users.User


fun authorizationRules(): ConfigBuilder.AuthorizationRulesBlock<Ctx, Data>.() -> Unit = {
    commands {
        positive {
            rule(::userCanCreateOwnTodos)
        }
        negative {
        }
    }
    readModels {
        positive {
            rule(::authenticationIdentityCanReadUsers)
            rule(::userCanReadOwnTodos)
        }
        negative {
        }
    }
    readProperties {
        positive {
            rule(::userCanReadOwnTodoProperty)
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

fun allCanReadEventLog(argContextReader: ArgContextReader<Ctx, Data>): PositiveAuthorization {
    return PositiveAuthorization.Allow
}

fun userCanReadOwnTodoProperty(args: ArgsForPropertyAuth<Ctx, Data>): PositiveAuthorization {
    val actor = args.context.actor
    if (actor is GroupModelIdentity) {
        // Add property-specific authorization logic here if needed
    }
    return PositiveAuthorization.NoOpinion
}

fun userCanCreateOwnTodos(args: ArgCommandContextReader<*, Ctx, Data>): PositiveAuthorization {
    val actor = args.context.actor
    val createParams = args.command.params

    if (actor is GroupModelIdentity && args.command.event is CreateTodo &&
        createParams is CreateTodoParams &&
        createParams.user == actor.model.props
    ) {
        return PositiveAuthorization.Allow
    }
    return PositiveAuthorization.NoOpinion
}

fun authenticationIdentityCanReadUsers(args: ArgModelContextReader<Ctx, Data>): PositiveAuthorization {
    if (args.context.actor == AuthenticationIdentity && args.model.props is User) {
        return PositiveAuthorization.Allow
    }
    return PositiveAuthorization.NoOpinion
}

fun userCanReadOwnTodos(args: ArgModelContextReader<Ctx, Data>): PositiveAuthorization {
    val actor = args.context.actor
    val todo = args.model.props
    if (actor is GroupModelIdentity && todo is Todo && todo.user == actor.model.props) {
        return PositiveAuthorization.Allow
    }
    return PositiveAuthorization.NoOpinion
}
