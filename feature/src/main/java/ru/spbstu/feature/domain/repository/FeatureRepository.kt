package ru.spbstu.feature.domain.repository

import io.reactivex.Single
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.feature.domain.model.Tokens

interface FeatureRepository {
    fun auth(api: String, token: String): Single<PayShareResult<Tokens>>
}
