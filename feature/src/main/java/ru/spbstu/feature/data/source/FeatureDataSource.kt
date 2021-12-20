package ru.spbstu.feature.data.source

import io.reactivex.Single
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.feature.domain.model.Tokens

interface FeatureDataSource {
    fun auth(api: String, token: String): Single<PayShareResult<Tokens>>
}
