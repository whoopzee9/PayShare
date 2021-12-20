package ru.spbstu.feature.data.remote.model.response

import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import ru.spbstu.feature.domain.model.Event
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class EventResponse(
    @SerializedName("room")
    val room: RoomResponse,
    @SerializedName("purchases")
    val purchases: List<PurchasesResponse>,
    @SerializedName("is_your")
    val isYours: Boolean
)

@SuppressLint("NewApi")
fun EventResponse.toEvent(): Event {
    return Event(
        id = room.id,
        name = room.name,
        date = LocalDateTime.parse(room.date, DateTimeFormatter.ofPattern("dd.MM.yy HH:mm")),
        expenses = purchases.map { it.toExpense() },
        isClosed = room.isClosed,
        isYours = isYours
    )
}