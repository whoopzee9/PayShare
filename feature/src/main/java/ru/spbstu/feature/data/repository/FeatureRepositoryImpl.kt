package ru.spbstu.feature.data.repository

import io.reactivex.Single
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.feature.data.source.FeatureDataSource
import ru.spbstu.feature.domain.model.Tokens
import ru.spbstu.feature.domain.repository.FeatureRepository
import javax.inject.Inject

class FeatureRepositoryImpl @Inject constructor(private val featureDataSource: FeatureDataSource) :
    FeatureRepository {
    override fun auth(api: String, token: String): Single<PayShareResult<Tokens>> {
        return featureDataSource.auth(api, token)
    }
}
