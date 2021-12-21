package ru.spbstu.feature.domain.usecase

import io.reactivex.Single
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.feature.domain.model.EventInfo
import ru.spbstu.feature.domain.repository.FeatureRepository
import javax.inject.Inject

class GetEventInfoUseCase @Inject constructor(private val featureRepository: FeatureRepository) {
    operator fun invoke(id: Long): Single<PayShareResult<EventInfo>> {
        return featureRepository.getEvent(id)
    }
}