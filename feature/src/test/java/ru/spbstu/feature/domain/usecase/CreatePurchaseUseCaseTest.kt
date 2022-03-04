package ru.spbstu.feature.domain.usecase

import io.reactivex.Single
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.feature.domain.model.Expense
import ru.spbstu.feature.domain.repository.FeatureRepository

class CreatePurchaseUseCaseTest {
    private val repository = mock<FeatureRepository>()
    private val testRoomId = 1L
    private val testExpense = mock<Expense>()
    private val rxSinglePurchaseId: Single<PayShareResult<Any>> =
        Single.just(PayShareResult.Success(123L))

    @Test
    fun `should return created purchase id`() {
        val expectedPurchaseId = 123L
        Mockito.`when`(repository.createPurchase(roomId = testRoomId, expense = testExpense))
            .thenReturn(rxSinglePurchaseId)

        CreatePurchaseUseCase(featureRepository = repository).invoke(
            roomId = testRoomId,
            expense = testExpense
        ).test().assertValue {
            val actualId = when (it) {
                is PayShareResult.Success -> it.data
                else -> throw IllegalStateException("Illegal room number")
            }
            actualId == expectedPurchaseId
        }
    }
}
