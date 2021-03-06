package ru.spbstu.feature.domain.usecase

import io.reactivex.Single
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.feature.domain.model.Expense
import ru.spbstu.feature.domain.model.User
import ru.spbstu.feature.domain.repository.FeatureRepository
import javax.inject.Inject

class CreatePurchaseUseCase @Inject constructor(private val featureRepository: FeatureRepository) {
    operator fun invoke(roomId: Long, expense: Expense): Single<PayShareResult<Any>> {
        return featureRepository.createPurchase(roomId, expense)
    }
}
