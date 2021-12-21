package ru.spbstu.feature.domain.usecase

import io.reactivex.Single
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.feature.domain.repository.FeatureRepository
import javax.inject.Inject

class SetPurchaseJoinUseCase @Inject constructor(private val featureRepository: FeatureRepository) {
    operator fun invoke(
        roomId: Long,
        purchaseId: Long,
        participantId: Long,
        isJoined: Boolean
    ): Single<PayShareResult<Any>> {
        return featureRepository.setPurchaseJoin(roomId, purchaseId, participantId, isJoined)
    }
}