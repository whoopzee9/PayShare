package ru.spbstu.feature.domain.model

import ru.spbstu.common.base.BaseModel

data class User(
    override val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String
) : BaseModel(id) {
    override fun isContentEqual(other: BaseModel): Boolean {
        return other is User && this.firstName == other.firstName &&
                this.lastName == other.lastName && this.email == other.email
    }
}