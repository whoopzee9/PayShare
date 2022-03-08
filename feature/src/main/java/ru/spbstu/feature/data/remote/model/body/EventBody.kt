package ru.spbstu.feature.data.remote.model.body

import com.google.gson.annotations.SerializedName

data class EventBody(
    @SerializedName("room_name")
    val name: String,
    @SerializedName("room_date")
    val date: String
)
