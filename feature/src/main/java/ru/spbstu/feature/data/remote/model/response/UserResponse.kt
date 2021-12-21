package ru.spbstu.feature.data.remote.model.response

import com.google.gson.annotations.SerializedName
import ru.spbstu.feature.domain.model.User

data class UserResponse(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("second_name")
    val lastName: String,
    @SerializedName("image_url")
    val imageUrl: String
)

fun UserResponse.toUser() = User(
    firstName = firstName,
    lastName = lastName,
    imageUrl = imageUrl
)
