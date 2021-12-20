package ru.spbstu.feature.domain.usecase

import io.reactivex.Single
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.feature.domain.model.User
import ru.spbstu.feature.domain.repository.FeatureRepository
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(private val featureRepository: FeatureRepository) {
    operator fun invoke(): Single<PayShareResult<User>> {
        return featureRepository.getUserInfo()
    }
}