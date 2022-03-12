package ru.spbstu.feature.domain.usecase

import io.reactivex.Single
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.common.model.EventError
import ru.spbstu.feature.domain.repository.FeatureRepository

class SetPurchasePaidUseCaseTest {
    private val repository = mock<FeatureRepository>()
    private val roomId = 1L
    private val purchaseId = 1L
    private val participantId = 1L
    private val isPaid = true
    private val rxSingleTest: Single<PayShareResult<Any>> =
        Single.just(PayShareResult.Success(Any()))
    private val rxSingleError: Single<PayShareResult<Any>> =
        Single.just(PayShareResult.Error(EventError.UnknownError))

    @Test
    fun `should return success`() {
        Mockito.`when`(repository.setPurchasePaid(roomId, purchaseId, participantId, isPaid))
            .thenReturn(rxSingleTest)
        SetPurchasePaidUseCase(repository).invoke(roomId, purchaseId, participantId, isPaid).test()
            .assertValue {
                val isSuccess = when (it) {
                    is PayShareResult.Success -> true
                    else -> false
                }
                isSuccess
            }
    }

    @Test
    fun `should return error`() {
        Mockito.`when`(repository.setPurchasePaid(roomId, purchaseId, participantId, isPaid))
            .thenReturn(rxSingleError)
        SetPurchasePaidUseCase(repository).invoke(roomId, purchaseId, participantId, isPaid).test()
            .assertValue {
                val isFailure = when (it) {
                    is PayShareResult.Success -> false
                    else -> true
                }
                isFailure
            }
    }
}