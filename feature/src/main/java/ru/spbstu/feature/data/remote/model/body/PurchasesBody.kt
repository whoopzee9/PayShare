package ru.spbstu.feature.data.remote.model.body

import com.google.gson.annotations.SerializedName
import ru.spbstu.feature.data.remote.model.response.LocationResponse
import ru.spbstu.feature.domain.model.Expense
import ru.spbstu.feature.domain.model.User
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class PurchasesBody(
    @SerializedName("name")
    val name: String,
    @SerializedName("locate")
    val location: LocationResponse,
    @SerializedName("cost")
    val cost: Int
)
