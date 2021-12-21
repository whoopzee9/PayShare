package ru.spbstu.feature.data.remote.model.response

import com.google.gson.annotations.SerializedName

data class RoomInfoResponse(
    @SerializedName("owner_participant_id")
    val ownerParticipantId: Long,
    @SerializedName("participants")
    val participants: List<ParticipantResponse>,
    @SerializedName("purchases")
    val purchases: List<PurchasesResponse>
)