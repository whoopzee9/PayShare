package ru.spbstu.feature.domain.model

import ru.spbstu.common.base.BaseModel
import java.time.LocalDateTime

data class Expense(
    override val id: Long = -1,
    val name: String = "",
    val description: String = "",
    val isBought: Boolean = false,
    val buyer: User = User(),
    val date: LocalDateTime = LocalDateTime.now(),
    val price: Double = 0.0,
    val users: Map<User, Boolean> = emptyMap(),
    val purchaseShop: Shop = Shop(),
    val isPaid: Boolean = false, //todo: subject to change, change to what comrs from back
    val isSharing: Boolean = false //todo: subject to change, maybe check users list if current user is present (need to get curr user)
) : BaseModel(id) {
    override fun isContentEqual(other: BaseModel): Boolean {
        return other is Expense && this.name == other.name && this.description == other.description &&
                this.isBought == other.isBought && this.buyer == other.buyer &&
                this.date == other.date && this.price == other.price && this.users == other.users &&
                this.purchaseShop == other.purchaseShop  && this.isPaid == other.isPaid &&
                this.isSharing == other.isSharing
    }
}
