package ru.spbstu.feature.data.remote.api

import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import ru.spbstu.common.token.RefreshToken
import ru.spbstu.feature.data.remote.model.body.AuthBody
import ru.spbstu.feature.data.remote.model.response.TokensResponse
import ru.spbstu.feature.data.remote.model.response.UserResponse

interface FeatureApiService {
    @POST("/auth/login")
    fun auth(@Body authBody: AuthBody): Single<Response<TokensResponse>>

    @POST("/auth/logout")
    fun logout(@Body refreshToken: RefreshToken): Single<Response<Void>>

    @GET("/user")
    fun getUserInfo(): Single<Response<UserResponse>>
}
