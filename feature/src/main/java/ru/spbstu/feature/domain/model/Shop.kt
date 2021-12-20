package ru.spbstu.feature.domain.model

import ru.spbstu.common.base.BaseModel

data class Shop(
    override val id: Long = 0,
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val purchasedItems: List<Expense> = listOf(),
) : BaseModel(id) {
    override fun isContentEqual(other: BaseModel): Boolean {
        return other is Shop && this.name == other.name && this.latitude == other.latitude &&
                this.longitude == other.longitude && this.purchasedItems == other.purchasedItems
    }
}