package se.moshicon.klerkframework.todo_app.users

import dev.klerkframework.klerk.ActorIdentity

class GroupModelIdentity (
    val model: dev.klerkframework.klerk.Model<User>,
    val groups: List<String> = emptyList(),
) : ActorIdentity {
    override val type: Int = ActorIdentity.Companion.customType
    override val id: dev.klerkframework.klerk.ModelID<User> = model.id
    override val externalId: Long? = null
    override fun toString(): String = "model: $model groups: $groups"
}