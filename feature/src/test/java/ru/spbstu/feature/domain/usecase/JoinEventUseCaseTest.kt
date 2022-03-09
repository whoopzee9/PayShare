package ru.spbstu.feature.domain.usecase

import io.reactivex.Single
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.feature.domain.repository.FeatureRepository

class JoinEventUseCaseTest {
    private val repository = mock<FeatureRepository>()
    private val rxSingleTestResponse: Single<PayShareResult<Any>> =
        Single.just(PayShareResult.Success(Any()))


    @Test
    fun `should successfully join the event`() {
        val eventId = 9912L

        Mockito.`when`(repository.joinEvent(id = eventId)).thenReturn(rxSingleTestResponse)

        JoinEventUseCase(repository)
            .invoke(id = eventId).test().assertValue {
                it is PayShareResult.Success
            }
    }
}