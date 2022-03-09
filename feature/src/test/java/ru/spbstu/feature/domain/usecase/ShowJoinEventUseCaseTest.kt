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

class ShowJoinEventUseCaseTest {
    private val repository = mock<FeatureRepository>()
    private val simpleEvent = DomainStubClasses.event
    private val eventCode = "3354"

    @Test
    fun `should join to event`() {
        val expectedEvent = DomainStubClasses.event
        val rxSingleEmptyEvents: Single<PayShareResult<Event>> =
            Single.just(PayShareResult.Success(simpleEvent))

        Mockito.`when`(repository.showJoinEvent(code = eventCode)).thenReturn(rxSingleEmptyEvents)

        ShowJoinEventUseCase(repository).invoke(code = eventCode).test().assertValue {
            val repoEvent = when (it) {
                is PayShareResult.Success -> it.data
                else -> throw IllegalArgumentException("Not success status")
            }
            repoEvent == expectedEvent
        }
    }

    @Test
    fun `should return error`() {
        val rxSingleTestErrorResponse: Single<PayShareResult<Event>> =
            Single.just(PayShareResult.Error(EventError.UnknownError))

        Mockito.`when`(repository.showJoinEvent(code = eventCode))
            .thenReturn(rxSingleTestErrorResponse)
        ShowJoinEventUseCase(repository)
            .invoke(code = eventCode).test().assertValue {
                val isErrorReturned = when (it) {
                    is PayShareResult.Success -> true
                    else -> false
                }
                !isErrorReturned
            }
    }
}