package ru.spbstu.feature.domain.usecase

import io.reactivex.Single
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.feature.domain.model.Tokens
import ru.spbstu.feature.domain.repository.FeatureRepository
import javax.inject.Inject

class AuthUseCase @Inject constructor(private val featureRepository: FeatureRepository) {
    operator fun invoke(api: String, token: String): Single<PayShareResult<Tokens>> {
        return featureRepository.auth(api, token)
    }
}