package ru.spbstu.feature.data.remote.source

import io.reactivex.Single
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.common.model.EventError
import ru.spbstu.common.token.RefreshToken
import ru.spbstu.common.token.TokenRepositoryImpl
import ru.spbstu.feature.data.remote.api.FeatureApiService
import ru.spbstu.feature.data.remote.model.body.AuthBody
import ru.spbstu.feature.data.remote.model.body.EventBody
import ru.spbstu.feature.data.remote.model.body.EventJoinBody
import ru.spbstu.feature.data.remote.model.body.SetPurchaseJoinBody
import ru.spbstu.feature.data.remote.model.body.SetPurchasePaidBody
import ru.spbstu.feature.data.remote.model.response.toEvent
import ru.spbstu.feature.data.remote.model.response.toEventInfo
import ru.spbstu.feature.data.remote.model.response.toUser
import ru.spbstu.feature.data.source.FeatureDataSource
import ru.spbstu.feature.domain.model.Event
import ru.spbstu.feature.domain.model.EventInfo
import ru.spbstu.feature.domain.model.Expense
import ru.spbstu.feature.domain.model.Tokens
import ru.spbstu.feature.domain.model.User
import ru.spbstu.feature.domain.model.toPurchaseBody
import timber.log.Timber
import javax.inject.Inject

class FeatureDataSourceImpl @Inject constructor(private val featureApiService: FeatureApiService) :
    FeatureDataSource {

    @Inject
    lateinit var tokenRepositoryImpl: TokenRepositoryImpl

    override fun auth(api: String, token: String): Single<PayShareResult<Tokens>> {
        return featureApiService.auth(AuthBody(api, token)).map {
            when {
                it.isSuccessful -> {
                    val access = it.body()?.accessToken
                    val refresh = it.body()?.refreshToken
                    if (access == null || refresh == null) {
                        Timber.tag(TAG).e("One of tokens is null, response=$it")
                        PayShareResult.Error(EventError.UnknownError)
                    } else {
                        PayShareResult.Success(Tokens(access, refresh))
                    }
                }
                else -> {
                    PayShareResult.Error(EventError.UnknownError)
                }
            }
        }
    }

    override fun logout(refreshToken: String): Single<PayShareResult<Any>> {
        return featureApiService.logout(RefreshToken(refreshToken)).map {
            when {
                it.isSuccessful -> {
                    tokenRepositoryImpl.clearTokens()
                    PayShareResult.Success(it)
                }
                else -> {
                    PayShareResult.Error(EventError.UnknownError)
                }
            }
        }
    }

    override fun getUserInfo(): Single<PayShareResult<User>> {
        return featureApiService.getUserInfo().map {
            when {
                it.isSuccessful -> {
                    val res = it.body()
                    if (res != null) {
                        PayShareResult.Success(res.toUser())
                    } else {
                        PayShareResult.Error(EventError.UnknownError)
                    }
                }
                else -> {
                    PayShareResult.Error(EventError.UnknownError)
                }
            }
        }
    }

    override fun getEvents(): Single<PayShareResult<List<Event>>> {
        return featureApiService.getEvents().map {
            when {
                it.isSuccessful -> {
                    val res = it.body()
                    if (res != null) {
                        if (res.rooms != null) {
                            PayShareResult.Success(res.rooms.map { response -> response.toEvent() })
                        } else {
                            PayShareResult.Success(listOf())
                        }
                    } else {
                        PayShareResult.Error(EventError.UnknownError)
                    }
                }
                else -> {
                    PayShareResult.Error(EventError.UnknownError)
                }
            }
        }
    }

    override fun createEvent(name: String, date: String): Single<PayShareResult<Long>> {
        return featureApiService.createEvent(EventBody(name, date)).map {
            when {
                it.isSuccessful -> {
                    val res = it.body()
                    if (res != null) {
                        PayShareResult.Success(res.id)
                    } else {
                        PayShareResult.Error(EventError.UnknownError)
                    }
                }
                else -> {
                    PayShareResult.Error(EventError.UnknownError)
                }
            }
        }
    }

    override fun showJoinEvent(code: String): Single<PayShareResult<Event>> {
        return featureApiService.showJoinEvent(EventJoinBody(code)).map {
            when {
                it.isSuccessful -> {
                    val res = it.body()
                    if (res != null) {
                        PayShareResult.Success(res.room.toEvent())
                    } else {
                        PayShareResult.Error(EventError.EventNotFound)
                    }
                }
                else -> {
                    PayShareResult.Error(EventError.EventNotFound)
                }
            }
        }
    }

    override fun joinEvent(id: Long): Single<PayShareResult<Any>> {
        return featureApiService.joinEvent(id).map {
            when {
                it.isSuccessful -> {
                    PayShareResult.Success(it)
                }
                else -> {
                    PayShareResult.Error(EventError.EventNotFound)
                }
            }
        }
    }

    override fun getHistory(): Single<PayShareResult<List<Event>>> {
        return featureApiService.getHistory().map {
            when {
                it.isSuccessful -> {
                    val res = it.body()
                    if (res != null) {
                        if (res.rooms != null) {
                            PayShareResult.Success(res.rooms.map { response -> response.toEvent() })
                        } else {
                            PayShareResult.Success(listOf())
                        }
                    } else {
                        PayShareResult.Error(EventError.UnknownError)
                    }
                }
                else -> {
                    PayShareResult.Error(EventError.UnknownError)
                }
            }
        }
    }

    override fun getEvent(id: Long): Single<PayShareResult<EventInfo>> {
        return featureApiService.getEvent(id).map {
            when {
                it.isSuccessful -> {
                    val res = it.body()
                    if (res != null) {
                        PayShareResult.Success(res.toEventInfo())
                    } else {
                        PayShareResult.Error(EventError.UnknownError)
                    }
                }
                else -> {
                    PayShareResult.Error(EventError.UnknownError)
                }
            }
        }
    }

    override fun setPurchasePaid(
        roomId: Long,
        purchaseId: Long,
        participantId: Long,
        isPaid: Boolean
    ): Single<PayShareResult<Any>> {
        return featureApiService.setPurchasePaid(
            roomId,
            purchaseId,
            SetPurchasePaidBody(participantId, isPaid)
        ).map {
            when {
                it.isSuccessful -> {
                    PayShareResult.Success(it)
                }
                else -> {
                    PayShareResult.Error(EventError.EventNotFound)
                }
            }
        }
    }

    override fun createPurchase(roomId: Long, expense: Expense): Single<PayShareResult<Any>> {
        return featureApiService.createPurchase(roomId, expense.toPurchaseBody()).map {
            when {
                it.isSuccessful -> {
                    PayShareResult.Success(it)
                }
                else -> {
                    PayShareResult.Error(EventError.UnknownError)
                }
            }
        }
    }

    override fun getRoomCode(id: Long): Single<PayShareResult<Long>> {
        return featureApiService.getRoomCode(id).map {
            when {
                it.isSuccessful -> {
                    val res = it.body()
                    if (res != null) {
                        PayShareResult.Success(res.code)
                    } else {
                        PayShareResult.Error(EventError.UnknownError)
                    }
                }
                else -> {
                    PayShareResult.Error(EventError.UnknownError)
                }
            }
        }
    }

    override fun deleteRoom(roomId: Long): Single<PayShareResult<Any>> {
        return featureApiService.deleteRoom(roomId).map {
            when {
                it.isSuccessful -> {
                    PayShareResult.Success(it)
                }
                else -> {
                    PayShareResult.Error(EventError.UnknownError)
                }
            }
        }
    }

    override fun deletePurchase(roomId:Long, purchaseId: Long): Single<PayShareResult<Any>> {
        return featureApiService.deletePurchase(roomId, purchaseId).map {
            when {
                it.isSuccessful -> {
                    PayShareResult.Success(it)
                }
                else -> {
                    PayShareResult.Error(EventError.UnknownError)
                }
            }
        }
    }

    override fun setPurchaseJoin(
        roomId: Long,
        purchaseId: Long,
        participantId: Long,
        isJoined: Boolean
    ): Single<PayShareResult<Any>> {
        return featureApiService.setPurchaseJoin(
            roomId,
            purchaseId,
            SetPurchaseJoinBody(participantId, isJoined)
        ).map {
            when {
                it.isSuccessful -> {
                    PayShareResult.Success(it)
                }
                else -> {
                    PayShareResult.Error(EventError.EventNotFound)
                }
            }
        }
    }

    private companion object {
        private val TAG = FeatureDataSourceImpl::class.simpleName
    }
}
