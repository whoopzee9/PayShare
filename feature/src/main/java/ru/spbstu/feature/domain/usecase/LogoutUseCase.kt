package ru.spbstu.feature.domain.usecase

import io.reactivex.Single
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.feature.domain.repository.FeatureRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(private val featureRepository: FeatureRepository) {
    operator fun invoke(refreshToken: String): Single<PayShareResult<Any>> {
        return featureRepository.logout(refreshToken)
    }
}