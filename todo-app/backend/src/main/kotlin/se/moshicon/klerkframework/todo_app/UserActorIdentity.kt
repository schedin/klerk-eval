package se.moshicon.klerkframework.todo_app

import dev.klerkframework.klerk.ActorIdentity

/**
 * Custom ActorIdentity implementation for user authentication
 * This represents a user that has been authenticated via JWT
 */
data class UserActorIdentity(
    val username: String,
    val groups: List<String> = listOf()
) : ActorIdentity {
    override val id: String = username
    override val displayName: String = username
    override val isSystem: Boolean = false
}
