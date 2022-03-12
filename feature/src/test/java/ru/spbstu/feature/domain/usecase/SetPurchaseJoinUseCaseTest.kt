package ru.spbstu.feature.domain.usecase

import io.reactivex.Single
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.feature.domain.repository.FeatureRepository

class SetPurchaseJoinUseCaseTest {
    private val repository = mock<FeatureRepository>()
    private val rxSingleTestResponse: Single<PayShareResult<Any>> =
        Single.just(PayShareResult.Success(Any()))
    private val roomId = 11L
    private val purchaseId = 11L
    private val participantId = 11L

    @Test
    fun `should successfully join purchase`() {
        Mockito.`when`(
            repository.setPurchaseJoin(
                roomId = roomId,
                purchaseId = purchaseId,
                participantId = participantId,
                isJoined = true
            )
        ).thenReturn(rxSingleTestResponse)

        SetPurchaseJoinUseCase(repository)
            .invoke(
                roomId = roomId,
                purchaseId = purchaseId,
                participantId = participantId,
                isJoined = true
            ).test().assertValue {
                it is PayShareResult.Success
            }
    }
}