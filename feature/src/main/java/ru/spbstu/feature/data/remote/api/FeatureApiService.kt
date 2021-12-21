package ru.spbstu.feature.data.remote.api

import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.spbstu.common.token.RefreshToken
import ru.spbstu.feature.data.remote.model.body.AuthBody
import ru.spbstu.feature.data.remote.model.body.EventBody
import ru.spbstu.feature.data.remote.model.body.EventJoinBody
import ru.spbstu.feature.data.remote.model.response.EventIdResponse
import ru.spbstu.feature.data.remote.model.response.EventInfoResponse
import ru.spbstu.feature.data.remote.model.response.EventResponse
import ru.spbstu.feature.data.remote.model.response.RoomQRWrapper
import ru.spbstu.feature.data.remote.model.response.RoomWrapper
import ru.spbstu.feature.data.remote.model.response.TokensResponse
import ru.spbstu.feature.data.remote.model.response.UserResponse

interface FeatureApiService {
    @POST("/auth/login")
    fun auth(@Body authBody: AuthBody): Single<Response<TokensResponse>>

    @POST("/auth/logout")
    fun logout(@Body refreshToken: RefreshToken): Single<Response<Void>>

    @GET("/user")
    fun getUserInfo(): Single<Response<UserResponse>>

    @GET("/user/room/opened")
    fun getEvents(): Single<Response<RoomWrapper>>

    @POST("/user/room")
    fun createEvent(@Body eventBody: EventBody): Single<Response<EventIdResponse>>

    @POST("/user/room/join")
    fun showJoinEvent(@Body eventJoinBody: EventJoinBody): Single<Response<RoomQRWrapper>>

    @POST("/user/room/join/{room_id}")
    fun joinEvent(@Path("room_id") id: Long): Single<Response<Void>>

    @GET("/user/room/closed")
    fun getHistory(): Single<Response<RoomWrapper>>

    @GET("/user/room/{room_id}")
    fun getEvent(@Path("room_id") roomId: Long): Single<Response<EventInfoResponse>>
}
