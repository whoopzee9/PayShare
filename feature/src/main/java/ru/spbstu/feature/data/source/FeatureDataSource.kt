package ru.spbstu.feature.data.source

import io.reactivex.Single
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.feature.domain.model.Tokens
import ru.spbstu.feature.domain.model.User

interface FeatureDataSource {
    fun auth(api: String, token: String): Single<PayShareResult<Tokens>>
    fun logout(refreshToken: String): Single<PayShareResult<Any>>
    fun getUserInfo(): Single<PayShareResult<User>>
}
