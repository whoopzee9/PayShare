package ru.spbstu.feature.data.remote.model.body

import com.google.gson.annotations.SerializedName

data class SetPurchaseJoinBody(
    @SerializedName("participant_id")
    val participantId: Long,
    @SerializedName("join")
    val isJoined: Boolean
)