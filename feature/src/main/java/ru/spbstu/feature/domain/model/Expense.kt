package ru.spbstu.feature.domain.model

import ru.spbstu.common.base.BaseModel
import ru.spbstu.feature.data.remote.model.body.PurchasesBody
import ru.spbstu.feature.data.remote.model.response.LocationResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

data class Expense(
    override val id: Long = -1,
    val name: String = "",
    val description: String = "",
    val isBought: Boolean = false,
    val buyer: User = User(),
    val date: LocalDateTime = LocalDateTime.now(),
    val price: Double = 0.0,
    val users: Map<Long, Boolean> = emptyMap(),
    val purchaseShop: Shop = Shop()
) : BaseModel(id) {
    override fun isContentEqual(other: BaseModel): Boolean {
        return other is Expense && this.name == other.name && this.description == other.description &&
            this.isBought == other.isBought && this.buyer == other.buyer &&
            this.date == other.date && this.price == other.price && this.users == other.users &&
            this.purchaseShop == other.purchaseShop
    }
}

fun Expense.toPurchaseBody(): PurchasesBody {
    return PurchasesBody(
        name = name,
        location = LocationResponse(
            purchaseShop.latitude,
            purchaseShop.longitude,
            name,
            date = date.format(DateTimeFormatter.ofPattern("dd.MM.yy HH:mm")),
            description
        ),
        cost = price.roundToInt()
    )
}
