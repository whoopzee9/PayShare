package ru.spbstu.feature.data.remote.model.response

import com.google.gson.annotations.SerializedName
import ru.spbstu.feature.domain.model.Expense
import ru.spbstu.feature.domain.model.User
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class PurchasesResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("owner_id")
    val ownerId: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("locate")
    val location: LocationResponse,
    @SerializedName("cost")
    val cost: Int
)

fun PurchasesResponse.toExpense(): Expense {
    return Expense(
        id = id,
        name = name,
        description = location.description,
        buyer = User(id = ownerId),
        date = LocalDateTime.parse(location.date, DateTimeFormatter.ofPattern("dd.MM.yy HH:mm")),
        price = cost / 100.0,
        purchaseShop = location.toShop()
    )
}