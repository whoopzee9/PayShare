package ru.spbstu.feature.domain.usecase

import io.reactivex.Single
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.feature.domain.model.Event
import ru.spbstu.feature.domain.repository.FeatureRepository
import javax.inject.Inject

class GetHistoryUseCase @Inject constructor(private val featureRepository: FeatureRepository) {
    operator fun invoke(): Single<PayShareResult<List<Event>>> {
        return featureRepository.getHistory()
    }
}