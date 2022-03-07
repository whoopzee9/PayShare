package ru.spbstu.feature.data.remote.model.response

import com.google.gson.annotations.SerializedName

data class RoomResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("room_name")
    val name: String,
    @SerializedName("room_date")
    val date: String,
    @SerializedName("close")
    val isClosed: Boolean
)