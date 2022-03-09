package ru.spbstu.feature.domain.usecase

import io.reactivex.Single
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.common.model.EventError
import ru.spbstu.feature.domain.model.Event
import ru.spbstu.feature.domain.repository.FeatureRepository


class GetEventsUseCaseTest {
    private val repository = mock<FeatureRepository>()
    private val simpleEvent = domainStubClasses.event

    @Test
    fun `should return empty event list`() {
        val expectedEvents = emptyList<Event>()
        val rxSingleEmptyEvents: Single<PayShareResult<List<Event>>> =
            Single.just(PayShareResult.Success(expectedEvents))

        Mockito.`when`(repository.getEvents()).thenReturn(rxSingleEmptyEvents)

        GetEventsUseCase(repository).invoke().test().assertValue {
            val repoEvents = when (it) {
                is PayShareResult.Success -> it.data
                else -> throw IllegalArgumentException("Not success status")
            }
            repoEvents == expectedEvents
        }
    }

    @Test
    fun `should return not empty event list`() {
        val events = listOf(
            simpleEvent,
            simpleEvent.copy(
                id = 2,
                expenses = listOf(domainStubClasses.expense.copy(price = 300.0))
            )
        )
        val rxSingleEvents: Single<PayShareResult<List<Event>>> =
            Single.just(PayShareResult.Success(events))

        Mockito.`when`(repository.getEvents()).thenReturn(rxSingleEvents)
        GetEventsUseCase(repository).invoke().test().assertValue {
            val repoEvents = when (it) {
                is PayShareResult.Success -> it.data
                else -> throw IllegalArgumentException("Not success status")
            }
            repoEvents == events
        }
    }

    @Test
    fun `should return connection error`() {
        val rxSingleEvents: Single<PayShareResult<List<Event>>> =
            Single.just(PayShareResult.Error(EventError.ConnectionError))

        Mockito.`when`(repository.getEvents()).thenReturn(rxSingleEvents)

        GetEventsUseCase(repository).invoke().test().assertValue {
            val result = when (it) {
                is PayShareResult.Success -> {
                    false
                }
                is PayShareResult.Error -> {
                    it.error == EventError.ConnectionError
                }
            }
            result
        }
    }
}