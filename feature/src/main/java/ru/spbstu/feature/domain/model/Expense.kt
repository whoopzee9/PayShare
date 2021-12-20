package ru.spbstu.feature.domain.model

import ru.spbstu.common.base.BaseModel
import java.time.LocalDateTime

data class Expense(
    override val id: Long,
    val name: String,
    val description: String,
    val isBought: Boolean,
    val buyer: User,
    val date: LocalDateTime,
    val price: Double,
    val users: List<User>,
    val purchaseShop: Shop,
    val isPaid: Boolean, //todo: subject to change, change to what comrs from back
    val isSharing: Boolean //todo: subject to change, maybe check users list if current user is present (need to get curr user)
) : BaseModel(id) {
    override fun isContentEqual(other: BaseModel): Boolean {
        return other is Expense && this.name == other.name && this.description == other.description &&
                this.isBought == other.isBought && this.buyer == other.buyer &&
                this.date == other.date && this.price == other.price && this.users == other.users &&
                this.purchaseShop == other.purchaseShop && this.isPaid == other.isPaid &&
                this.isSharing == other.isSharing
    }
}