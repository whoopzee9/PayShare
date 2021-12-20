package ru.spbstu.feature.data.remote.source

import io.reactivex.Single
import ru.spbstu.common.error.PayShareResult
import ru.spbstu.common.model.EventError
import ru.spbstu.feature.data.remote.api.FeatureApiService
import ru.spbstu.feature.data.remote.model.body.AuthBody
import ru.spbstu.feature.data.source.FeatureDataSource
import ru.spbstu.feature.domain.model.Tokens
import timber.log.Timber
import javax.inject.Inject

class FeatureDataSourceImpl @Inject constructor(private val featureApiService: FeatureApiService) :
    FeatureDataSource {
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

    private companion object {
        private val TAG = FeatureDataSourceImpl::class.simpleName
    }
}
