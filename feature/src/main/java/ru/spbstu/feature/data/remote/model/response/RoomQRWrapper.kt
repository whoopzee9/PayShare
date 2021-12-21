package ru.spbstu.feature.data.remote.model.response

import com.google.gson.annotations.SerializedName

data class RoomQRWrapper(
    @SerializedName("room")
    val room: EventResponse
)