package se.moshicon.klerkframework.todo_app

import dev.klerkframework.klerk.datatypes.BooleanContainer
import dev.klerkframework.klerk.datatypes.StringContainer

data class Todo(
    val todoID: TodoID,
    val title: TodoTitle,
    val description: TodoDescription,
    val completed: TodoCompletedStatus,
//    val priority: TodoPriority,
)

//class TodoPriority(value: Int) : IntContainer(value) {
//    override val min = 0
//    override val max = 10
//}

class TodoID(value: String) : StringContainer(value) {
    override val minLength = 36
    override val maxLength = 36
    override val maxLines = 1
}

class TodoTitle(value: String) : StringContainer(value) {
    override val minLength = 0
    override val maxLength = 100
    override val maxLines = 1
}

class TodoDescription(value: String) : StringContainer(value) {
    override val minLength = 0
    override val maxLength = 100000
    override val maxLines = Int.MAX_VALUE
}

class TodoCompletedStatus constructor(value: Boolean) : BooleanContainer(value) {
}
//sealed class TodoCompletedStatus protected constructor(value: Boolean) : BooleanContainer(value)
//
////object TodoIsCompleted : TodoCompletedStatus(true)
//object TodoIsNotCompleted : TodoCompletedStatus(false)