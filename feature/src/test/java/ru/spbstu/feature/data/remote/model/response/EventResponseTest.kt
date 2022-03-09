package ru.spbstu.feature.data.remote.model.response

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import ru.spbstu.feature.domain.model.Event
import ru.spbstu.feature.domain.model.Expense
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class  EventResponseTest {
    private val mockedRoomResponse = mock<RoomResponse>()
    private val mockedEventResponse =
        EventResponse(room = mockedRoomResponse, purchases = emptyList(), isYours = false)

    private val date = "08.02.22 14:01"
    private val testDomainEvent = Event(
        id = 1,
        name = "room",
        date = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yy HH:mm")),
        expenses = emptyList(),
        isClosed = false,
        isYours = false
    )
    private val testRoomResponse =
        RoomResponse(id = 1, name = "room", date = date, isClosed = false)
    private val testEventResponse =
        EventResponse(room = testRoomResponse, purchases = emptyList(), isYours = false)

    @Test
    fun `maps EventResponse to Event domain model`() {

        val mappedEvent = testEventResponse.toEvent()

        assertEquals(mappedEvent, testDomainEvent)
    }
    // TODO
/*

    @Test
    fun `should return correct data when parsing date`() {
        val date = "18.04.22 14:01"
        Mockito.`when`(testEventResponse.room.date).thenReturn(date)

        val localDateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yy HH:mm"))
        val mappedEvent = testEventResponse.toEvent()
        Mockito.`when`(mappedEvent.date).thenReturn(localDateTime)

        assertEquals(mappedEvent, testDomainEvent)
    }
*/

    @Test
    fun `should return DateTimeParseException when parsing date`() {
        val date = "08.032.22 14:01"
        Mockito.`when`(mockedEventResponse.room.date).thenReturn(date)

        assertThrows(DateTimeParseException::class.java) {
            mockedEventResponse.toEvent()
        }
    }

    @Test
    fun `should return empty purchase list when event purchase is null`() {
        val eventResponse =
            EventResponse(room = testRoomResponse, purchases = null, isYours = false)
        val mappedEvent = eventResponse.toEvent()
        assertEquals(mappedEvent.expenses, emptyList<Expense>())
    }

    @Test
    fun `should return purchase list when event purchase not null`() {
        val purchaseResponse = PurchasesResponse(
            id = 1,
            ownerId = 1,
            name = "name",
            location = LocationResponse(
                latitude = 0.0,
                longitude = 0.0,
                name = "name",
                date = date,
                description = ""
            ),
            cost = 1
        )
        val purchasesResponse = listOf(
            purchaseResponse, purchaseResponse.copy(id = 2), purchaseResponse.copy(id = 3)
        )
        val eventResponse =
            EventResponse(room = testRoomResponse, purchases = purchasesResponse, isYours = false)

        val mappedEvent = eventResponse.toEvent()

        assertEquals(mappedEvent.expenses, purchasesResponse.map { it.toExpense() }.toList())
    }
}