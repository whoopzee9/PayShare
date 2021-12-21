package ru.spbstu.feature.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.spbstu.common.base.BaseModel

@Parcelize
data class User(
    override val id: Long = 0,
    val firstName: String = "",
    val lastName: String = "",
    val imageUrl: String = ""
) : BaseModel(id), Parcelable {
    override fun isContentEqual(other: BaseModel): Boolean {
        return other is User && this.firstName == other.firstName &&
            this.lastName == other.lastName && this.imageUrl == other.imageUrl
    }

    fun getFullName(): String {
        return "$lastName $firstName"
    }
}
