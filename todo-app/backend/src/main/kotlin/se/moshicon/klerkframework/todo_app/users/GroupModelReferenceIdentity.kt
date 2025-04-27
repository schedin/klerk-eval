package se.moshicon.klerkframework.todo_app.users

import dev.klerkframework.klerk.ActorIdentity

class GroupModelReferenceIdentity<User : Any>(
    private val modelId: dev.klerkframework.klerk.ModelID<User>,
    val groups: List<String> = emptyList(),
) : ActorIdentity {
    override val type: Int = dev.klerkframework.klerk.ActorIdentity.Companion.customType
    override val id: dev.klerkframework.klerk.ModelID<User> = modelId
    override val externalId: Long? = null
    override fun toString(): String = "model id: $modelId groups: $groups"
}