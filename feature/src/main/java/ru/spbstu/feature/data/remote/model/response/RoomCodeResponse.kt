package ru.spbstu.feature.data.remote.model.response

import com.google.gson.annotations.SerializedName

data class RoomCodeResponse(
    @SerializedName("code")
    val code: Long
)
