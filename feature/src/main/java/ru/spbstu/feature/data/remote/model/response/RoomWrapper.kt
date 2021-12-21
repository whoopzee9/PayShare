package ru.spbstu.feature.data.remote.model.response

import com.google.gson.annotations.SerializedName

data class RoomWrapper(
    @SerializedName("rooms")
    val rooms: List<EventResponse>?
)