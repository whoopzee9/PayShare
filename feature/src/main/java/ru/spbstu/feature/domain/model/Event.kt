package ru.spbstu.feature.domain.model

import ru.spbstu.common.base.BaseModel
import java.time.LocalDateTime

data class Event(
    override val id: Long = 0,
    val name: String = "",
    val date: LocalDateTime = LocalDateTime.now(),
    val expenses: List<Expense> = listOf(),
    val isClosed: Boolean = false,
    val isYours: Boolean = false
) : BaseModel(id) {
    override fun isContentEqual(other: BaseModel): Boolean {
        return other is Event && this.name == other.name &&
                this.date == other.date && this.expenses == other.expenses &&
                this.isClosed == other.isClosed && this.isYours == other.isYours
    }
}