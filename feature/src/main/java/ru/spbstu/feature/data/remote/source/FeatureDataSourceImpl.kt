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
import ru.spbstu.feature.data.remote.model.response.toEvent
import ru.spbstu.feature.data.remote.model.response.toUser
import ru.spbstu.feature.data.source.FeatureDataSource
import ru.spbstu.feature.domain.model.Event
import ru.spbstu.feature.domain.model.Tokens
import ru.spbstu.feature.domain.model.User
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

    override fun joinEvent(code: String): Single<PayShareResult<Long>> {
        return featureApiService.joinEvent(EventJoinBody(code)).map {
            when {
                it.isSuccessful -> {
                    val res = it.body()
                    if (res != null) {
                        PayShareResult.Success(res.id)
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

    private companion object {
        private val TAG = FeatureDataSourceImpl::class.simpleName
    }
}
