package ru.spbstu.feature.domain.repository

import io.reactivex.Single
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.feature.domain.model.Event
import ru.spbstu.feature.domain.model.Expense
import ru.spbstu.feature.domain.model.Tokens
import ru.spbstu.feature.domain.model.User

interface FeatureRepository {
    fun auth(api: String, token: String): Single<PayShareResult<Tokens>>
    fun logout(refreshToken: String): Single<PayShareResult<Any>>
    fun getUserInfo(): Single<PayShareResult<User>>
    fun getEvents(): Single<PayShareResult<List<Event>>>
    fun createEvent(name: String, date: String): Single<PayShareResult<Long>>
    fun joinEvent(code: String): Single<PayShareResult<Long>>
    fun createPurchase(roomId: Long, expense: Expense): Single<PayShareResult<Any>>
}
