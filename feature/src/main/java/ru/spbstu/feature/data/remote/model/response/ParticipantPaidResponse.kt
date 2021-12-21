package ru.spbstu.feature.data.remote.model.response

import com.google.gson.annotations.SerializedName

data class ParticipantPaidResponse(
    @SerializedName("participant_id")
    val participantId: Long,
    @SerializedName("paid")
    val isPaid: Boolean
)