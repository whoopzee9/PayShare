package ru.spbstu.feature.domain.model

import ru.spbstu.common.base.BaseModel

data class UserBuyed(
    val user: User,
    val isBought: Boolean = false,
    override val id: Long = -1
) : BaseModel(id) {
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun isContentEqual(other: BaseModel): Boolean {
        return other is UserBuyed && this.user == other.user &&
            this.isBought == other.isBought
    }
}
