package ru.spbstu.feature.domain.usecase

import io.reactivex.Single
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.common.model.EventError
import ru.spbstu.feature.domain.DomainStubClasses
import ru.spbstu.feature.domain.model.Event
import ru.spbstu.feature.domain.repository.FeatureRepository

class GetHistoryUseCaseTest {
    private val repository = mock<FeatureRepository>()
    private val simpleEvent = DomainStubClasses.event

    @Test
    fun `should return empty history event list`() {
        val expectedHistoryEvents = emptyList<Event>()
        val rxSingleEmptyEvents: Single<PayShareResult<List<Event>>> =
            Single.just(PayShareResult.Success(expectedHistoryEvents))

        Mockito.`when`(repository.getHistory()).thenReturn(rxSingleEmptyEvents)

        GetHistoryUseCase(repository).invoke().test().assertValue {
            val repoEvents = when (it) {
                is PayShareResult.Success -> it.data
                else -> throw IllegalArgumentException("Not success status")
            }
            repoEvents == expectedHistoryEvents
        }
    }


    @Test
    fun `should return not empty history event list`() {
        val events = listOf(
            simpleEvent,
            simpleEvent.copy(
                id = 24654,
                expenses = listOf(DomainStubClasses.expense.copy(name = "Event33"))
            )
        )
        val rxSingleEvents: Single<PayShareResult<List<Event>>> =
            Single.just(PayShareResult.Success(events))

        Mockito.`when`(repository.getHistory()).thenReturn(rxSingleEvents)
        GetHistoryUseCase(repository).invoke().test().assertValue {
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

        Mockito.`when`(repository.getHistory()).thenReturn(rxSingleEvents)

        GetHistoryUseCase(repository).invoke().test().assertValue {
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