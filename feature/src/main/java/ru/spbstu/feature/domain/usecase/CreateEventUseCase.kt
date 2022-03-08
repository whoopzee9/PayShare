package ru.spbstu.feature.domain.usecase

import io.reactivex.Single
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.feature.domain.repository.FeatureRepository
import javax.inject.Inject

class CreateEventUseCase @Inject constructor(private val featureRepository: FeatureRepository) {
    operator fun invoke(name: String, date: String): Single<PayShareResult<Long>> {
        return featureRepository.createEvent(name, date)
    }
}