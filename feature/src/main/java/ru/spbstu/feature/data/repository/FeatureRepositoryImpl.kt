package ru.spbstu.feature.data.repository

import io.reactivex.Single
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.feature.data.source.FeatureDataSource
import ru.spbstu.feature.domain.model.Event
import ru.spbstu.feature.domain.model.EventInfo
import ru.spbstu.feature.domain.model.Expense
import ru.spbstu.feature.domain.model.Tokens
import ru.spbstu.feature.domain.model.User
import ru.spbstu.feature.domain.repository.FeatureRepository
import javax.inject.Inject

class FeatureRepositoryImpl @Inject constructor(private val featureDataSource: FeatureDataSource) :
    FeatureRepository {
    override fun auth(api: String, token: String): Single<PayShareResult<Tokens>> {
        return featureDataSource.auth(api, token)
    }

    override fun logout(refreshToken: String): Single<PayShareResult<Any>> {
        return featureDataSource.logout(refreshToken)
    }

    override fun getUserInfo(): Single<PayShareResult<User>> {
        return featureDataSource.getUserInfo()
    }

    override fun getEvents(): Single<PayShareResult<List<Event>>> {
        return featureDataSource.getEvents()
    }

    override fun createEvent(name: String, date: String): Single<PayShareResult<Long>> {
        return featureDataSource.createEvent(name, date)
    }

    override fun showJoinEvent(code: String): Single<PayShareResult<Event>> {
        return featureDataSource.showJoinEvent(code)
    }

    override fun joinEvent(id: Long): Single<PayShareResult<Any>> {
        return featureDataSource.joinEvent(id)
    }

    override fun getHistory(): Single<PayShareResult<List<Event>>> {
        return featureDataSource.getHistory()
    }

    override fun getEvent(id: Long): Single<PayShareResult<EventInfo>> {
        return featureDataSource.getEvent(id)
    }

    override fun setPurchasePaid(
        roomId: Long,
        purchaseId: Long,
        participantId: Long,
        isPaid: Boolean
    ): Single<PayShareResult<Any>> {
        return featureDataSource.setPurchasePaid(roomId, purchaseId, participantId, isPaid)
    }

    override fun createPurchase(roomId: Long, expense: Expense): Single<PayShareResult<Any>> {
        return featureDataSource.createPurchase(roomId, expense)
    }

    override fun getRoomCode(id: Long): Single<PayShareResult<Long>> {
        return featureDataSource.getRoomCode(id)
    }

    override fun deleteRoom(roomId: Long): Single<PayShareResult<Any>> {
        return featureDataSource.deleteRoom(roomId)
    }
}
