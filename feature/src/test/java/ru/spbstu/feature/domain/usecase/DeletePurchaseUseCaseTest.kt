package ru.spbstu.feature.domain.usecase

import io.reactivex.Single
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.common.model.EventError
import ru.spbstu.feature.domain.repository.FeatureRepository

class DeletePurchaseUseCaseTest {
    private val repository = mock<FeatureRepository>()
    private val rxSingleTestPurchase: Single<PayShareResult<Any>> =
        Single.just(PayShareResult.Success(Any()))

    @Test
    fun `should successfully delete the purchase`() {
        val roomId = 2L
        val purchaseId = 22L

        Mockito.`when`(repository.deletePurchase(roomId = roomId, purchaseId = purchaseId))
            .thenReturn(rxSingleTestPurchase)

        DeletePurchaseUseCase(repository)
            .invoke(roomId = roomId, purchaseId = purchaseId).test().assertValue {
                it is PayShareResult.Success
            }
    }

    @Test
    fun `should return unknown error`() {
        val roomId = 22L
        val purchaseId = 1L
        val rxSingleTestPurchaseResponse: Single<PayShareResult<Any>> =
            Single.just(PayShareResult.Error(EventError.UnknownError))

        Mockito.`when`(repository.deletePurchase(roomId = roomId, purchaseId = purchaseId))
            .thenReturn(rxSingleTestPurchaseResponse)

        DeletePurchaseUseCase(repository)
            .invoke(roomId = roomId, purchaseId = purchaseId).test().assertValue {
                val result = when (it) {
                    is PayShareResult.Success -> {
                        false
                    }
                    is PayShareResult.Error -> {
                        it.error == EventError.UnknownError
                    }
                }
                result
            }
    }
}