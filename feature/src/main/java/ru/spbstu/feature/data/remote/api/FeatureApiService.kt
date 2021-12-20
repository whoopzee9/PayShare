package ru.spbstu.feature.data.remote.api

import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import ru.spbstu.feature.data.remote.model.body.AuthBody
import ru.spbstu.feature.data.remote.model.response.TokensResponse

interface FeatureApiService {
    @POST("/auth/login")
    fun auth(@Body authBody: AuthBody): Single<Response<TokensResponse>>
}
