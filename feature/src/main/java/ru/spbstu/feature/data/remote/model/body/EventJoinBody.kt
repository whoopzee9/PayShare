package ru.spbstu.feature.data.remote.model.body

import com.google.gson.annotations.SerializedName

data class EventJoinBody(
    @SerializedName("code")
    val code: String
)