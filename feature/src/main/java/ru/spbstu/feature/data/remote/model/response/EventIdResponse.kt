package ru.spbstu.feature.data.remote.model.response

import com.google.gson.annotations.SerializedName

data class EventIdResponse(
    @SerializedName("id")
    val id: Long
)