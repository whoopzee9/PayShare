package ru.spbstu.feature.domain.model

data class EventInfo(
    val yourParticipantId: Long,
    val ownerParticipantId: Long,
    val participants: List<User>,
    val purchases: List<Expense>
)