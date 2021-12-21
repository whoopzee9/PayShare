package ru.spbstu.feature.data.remote.model.response

import com.google.gson.annotations.SerializedName
import ru.spbstu.feature.domain.model.EventInfo

data class EventInfoResponse(
    @SerializedName("your_participant_id")
    val yourParticipantId: Long,
    @SerializedName("room_info")
    val roomInfo: RoomInfoResponse
)

fun EventInfoResponse.toEventInfo(): EventInfo {
    return EventInfo(
        yourParticipantId = yourParticipantId,
        ownerParticipantId = roomInfo.ownerParticipantId,
        participants = roomInfo.participants.map { it.toUser() },
        purchases = roomInfo.purchases?.map { it.toExpense() } ?: listOf()
    )
}
