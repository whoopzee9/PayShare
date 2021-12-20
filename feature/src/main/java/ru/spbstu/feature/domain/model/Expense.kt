package ru.spbstu.feature.domain.model

import ru.spbstu.common.base.BaseModel
import java.time.LocalDateTime

data class Expense(
    override val id: Long = -1,
    val name: String = "",
    val description: String = "",
    val isBought: Boolean = false,
    val buyer: User = User(0, "", "", "", ""),
    val date: LocalDateTime = LocalDateTime.now(),
    val price: Double = 0.0,
    val users: Map<User, Boolean> = emptyMap(),
    val purchaseShop: Shop = Shop(
        -1, "", 0.0, 0.0, emptyList()
    )
) : BaseModel(id) {
    override fun isContentEqual(other: BaseModel): Boolean {
        return other is Expense && this.name == other.name && this.description == other.description &&
            this.isBought == other.isBought && this.buyer == other.buyer &&
            this.date == other.date && this.price == other.price && this.users == other.users &&
            this.purchaseShop == other.purchaseShop
    }
}
