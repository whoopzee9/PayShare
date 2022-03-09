package ru.spbstu.feature.domain.usecase

import io.reactivex.Single
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.common.model.EventError
import ru.spbstu.feature.domain.repository.FeatureRepository

class CreateEventUseCaseTest {
    private val repository = mock<FeatureRepository>()
    private val rxSingleEvent: Single<PayShareResult<Long>> =
        Single.just(PayShareResult.Success(522333L))

    private val name = "new event"
    private val date = "20.01.2022"

    @Test
    fun `should return created event id`() {
        val expectedEventId = 522333L

        Mockito.`when`(repository.createEvent(name = name, date = date))
            .thenReturn(rxSingleEvent)

        CreateEventUseCase(featureRepository = repository).invoke(name = name, date = date).test()
            .assertValue {
                val actualId = when (it) {
                    is PayShareResult.Success -> it.data
                    else -> throw IllegalStateException("Illegal event number")
                }
                actualId == expectedEventId
            }
    }

    @Test
    fun `should return connection error`() {
        val rxSingleTestEventResponse: Single<PayShareResult<Long>> =
            Single.just(PayShareResult.Error(EventError.ConnectionError))

        Mockito.`when`(repository.createEvent(name = name, date = date))
            .thenReturn(rxSingleTestEventResponse)

        CreateEventUseCase(featureRepository = repository).invoke(name = name, date = date).test()
            .assertValue {
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