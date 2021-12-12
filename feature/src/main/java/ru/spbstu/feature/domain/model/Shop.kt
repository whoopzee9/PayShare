package ru.spbstu.feature.domain.model

import ru.spbstu.common.base.BaseModel

data class Shop(
    override val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val purchasedItems: List<Expense>,
) : BaseModel(id) {
    override fun isContentEqual(other: BaseModel): Boolean {
        return other is Shop && this.name == other.name && this.latitude == other.latitude &&
                this.longitude == other.longitude && this.purchasedItems == other.purchasedItems
    }
}