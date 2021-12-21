package ru.spbstu.feature.data.remote.model.body

import com.google.gson.annotations.SerializedName

data class SetPurchasePaidBody(
    @SerializedName("participant_id")
    val participantId: Long,
    @SerializedName("paid")
    val isPaid: Boolean
)