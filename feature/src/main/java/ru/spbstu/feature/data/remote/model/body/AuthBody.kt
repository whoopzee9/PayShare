package ru.spbstu.feature.data.remote.model.body

import com.google.gson.annotations.SerializedName

data class AuthBody(
    @SerializedName("auth_api")
    val api: String,
    @SerializedName("token")
    val token: String
)