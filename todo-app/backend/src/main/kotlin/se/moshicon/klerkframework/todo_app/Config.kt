package se.moshicon.klerkframework.todo_app

import dev.klerkframework.klerk.*
import dev.klerkframework.klerk.collection.ModelCollections
import dev.klerkframework.klerk.storage.Persistence
import dev.klerkframework.klerk.storage.SqlPersistence
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.sqlite.SQLiteDataSource
import se.moshicon.klerkframework.todo_app.notes.Todo
import se.moshicon.klerkframework.todo_app.notes.todoStateMachine
import se.moshicon.klerkframework.todo_app.users.User
import se.moshicon.klerkframework.todo_app.users.userStateMachine

class Ctx(
    override val actor: ActorIdentity,
    override val auditExtra: String? = null,
    override val time: Instant = Clock.System.now(),
    override val translator: Translator = DefaultTranslator(),
) : KlerkContext

// Helper function to check if actor is in a group
fun actorInGroup(group: String): (Ctx) -> Boolean = { ctx ->
    when (val actor = ctx.actor) {
        is UserActorIdentity -> group in actor.groups
        else -> false
    }
}

fun createConfig() = ConfigBuilder<Ctx, Data>(Data).build {
    authorization {
        // Define authorization rules based on user groups
        rule {
            // Allow admin group to do everything
            actorInGroup("admin") allows everything()

            // Allow users to create and manage todos
            actorInGroup("user") allows {
                create(Todo::class)
                read(Todo::class)
                update(Todo::class)
                delete(Todo::class)
            }

            // Allow guests to only read todos
            actorInGroup("guest") allows {
                read(Todo::class)
                read(User::class)
            }

            // Allow system to do everything (for initialization)
            isSystem() allows everything()
        }
    }
    managedModels {
        model(Todo::class, todoStateMachine, Data.todos)
        model(User::class, userStateMachine, Data.users)
    }
    persistence(createPersistence())
    contextProvider { actor -> Ctx(actor) }
}

object Data {
    val todos = ModelCollections<Todo, Ctx>()
    val users = ModelCollections<User, Ctx>()
}

private fun createPersistence(): Persistence {
    val dbFilePath =
        requireNotNull(System.getenv("DATABASE_PATH")) { "The environment variable 'DATABASE_PATH' must be set" }
    val ds =  SQLiteDataSource()
    ds.url = "jdbc:sqlite:$dbFilePath"
    return SqlPersistence(ds)
}
