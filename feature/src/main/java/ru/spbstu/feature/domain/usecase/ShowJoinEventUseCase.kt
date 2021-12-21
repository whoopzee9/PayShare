package ru.spbstu.feature.domain.usecase

import io.reactivex.Single
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.feature.domain.model.Event
import ru.spbstu.feature.domain.repository.FeatureRepository
import javax.inject.Inject

class ShowJoinEventUseCase @Inject constructor(private val featureRepository: FeatureRepository) {
    operator fun invoke(code: String): Single<PayShareResult<Event>> {
        return featureRepository.showJoinEvent(code)
    }
}