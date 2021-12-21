package ru.spbstu.feature.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.spbstu.common.base.BaseModel

@Parcelize
data class Shop(
    override val id: Long = 0,
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val purchasedItems: @RawValue List<Expense> = listOf(),
) : BaseModel(id), Parcelable {
    override fun isContentEqual(other: BaseModel): Boolean {
        return other is Shop && this.name == other.name && this.latitude == other.latitude &&
            this.longitude == other.longitude && this.purchasedItems == other.purchasedItems
    }
}
