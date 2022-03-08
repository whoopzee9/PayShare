package ru.spbstu.feature.domain.usecase

import io.reactivex.Single
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.feature.domain.model.EventInfo
import ru.spbstu.feature.domain.repository.FeatureRepository

class GetEventInfoUseCaseTest {
    private val repository = mock<FeatureRepository>()
    private val eventId = 1L
    private val testEvent = EventInfo()
    private val rxSingleTestUser: Single<PayShareResult<EventInfo>> =
        Single.just(PayShareResult.Success(testEvent))

    @Test
    fun `should return event info`() {
        Mockito.`when`(repository.getEvent(eventId)).thenReturn(rxSingleTestUser)
        GetEventInfoUseCase(repository).invoke(eventId).test().assertValue {
            val repoEvent = when (it) {
                is PayShareResult.Success -> it.data
                else -> throw IllegalArgumentException("Not success status")
            }
            repoEvent == testEvent
        }
    }
}