package ru.spbstu.feature.domain.model

import ru.spbstu.common.base.BaseModel
import java.time.LocalDateTime

data class Event(
    override val id: Long = 0,
    val code: String = "",
    val name: String = "",
    val date: LocalDateTime = LocalDateTime.now(),
    val expenses: List<Expense> = listOf(),
    val users: List<User> = listOf(),
    val isFavourite: Boolean = false
) : BaseModel(id) {
    override fun isContentEqual(other: BaseModel): Boolean {
        return other is Event && this.code == other.code && this.name == other.name &&
                this.date == other.date && this.expenses == other.expenses &&
                this.users == other.users && this.isFavourite == other.isFavourite
    }
}