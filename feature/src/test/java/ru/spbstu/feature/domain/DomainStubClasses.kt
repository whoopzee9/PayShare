package ru.spbstu.feature.domain

import ru.spbstu.feature.domain.model.*
import java.time.LocalDateTime


object DomainStubClasses {

    val purchaseShop = Shop(
        id = 1L,
        name = "shop1",
        latitude = 0.0,
        longitude = 0.0,
        purchasedItems = emptyList()
    )
    val user = User(
        id = 1L,
        firstName = "Georgiy",
        lastName = "Tolstolobikov",
        ""
    )
    val expense = Expense(
        name = "expense1",
        description = "description1",
        isBought = false,
        buyer = user,
        date = LocalDateTime.now(),
        price = 1334.0,
        users = mapOf(1L to true, 2L to false),
        purchaseShop = purchaseShop
    )
    val eventInfo = EventInfo(
        yourParticipantId = 1, ownerParticipantId = 1, participants = listOf(
            user, user.copy(id = 2)
        ), purchases = listOf(expense, expense.copy(id = 2323L))
    )


    val event: Event = Event(
        id = 1,
        name = "event",
        date = LocalDateTime.now(),
        expenses = listOf(expense),
        isClosed = false,
        isYours = false
    )
}