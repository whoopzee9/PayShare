package ru.spbstu.feature.domain.model

data class EventInfo(
    val yourParticipantId: Long = 0,
    val ownerParticipantId: Long = 0,
    val participants: List<User> = listOf(),
    val purchases: List<Expense> = listOf()
)