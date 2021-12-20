package ru.spbstu.feature.domain.model

import ru.spbstu.common.base.BaseModel

data class User(
    override val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val photoLink: String = ""
) : BaseModel(id) {
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun isContentEqual(other: BaseModel): Boolean {
        return other is User && this.firstName == other.firstName &&
            this.lastName == other.lastName && this.email == other.email &&
            this.photoLink == other.photoLink
    }

    fun getFullName(): String {
        return "$lastName $firstName"
    }
}
