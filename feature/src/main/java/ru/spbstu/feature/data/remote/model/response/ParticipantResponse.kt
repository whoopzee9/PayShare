package ru.spbstu.feature.data.remote.model.response

import com.google.gson.annotations.SerializedName
import ru.spbstu.feature.domain.model.User

data class ParticipantResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("image_url")
    val imageUrl: String
)

fun ParticipantResponse.toUser(): User {
    return User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        imageUrl = imageUrl
    )
}