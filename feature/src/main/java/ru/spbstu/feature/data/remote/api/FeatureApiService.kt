package ru.spbstu.feature.data.remote.api

import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import ru.spbstu.common.token.RefreshToken
import ru.spbstu.feature.data.remote.model.body.AuthBody
import ru.spbstu.feature.data.remote.model.body.EventBody
import ru.spbstu.feature.data.remote.model.body.EventJoinBody
import ru.spbstu.feature.data.remote.model.body.PurchasesBody
import ru.spbstu.feature.data.remote.model.body.SetPurchaseJoinBody
import ru.spbstu.feature.data.remote.model.body.SetPurchasePaidBody
import ru.spbstu.feature.data.remote.model.response.EventIdResponse
import ru.spbstu.feature.data.remote.model.response.EventInfoResponse
import ru.spbstu.feature.data.remote.model.response.RoomCodeResponse
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

    @PUT("/user/room/{room_id}/purchase/{purchase_id}/paid")
    fun setPurchasePaid(
        @Path("room_id") roomId: Long,
        @Path("purchase_id") purchaseId: Long,
        @Body setPurchasePaidBody: SetPurchasePaidBody
    ): Single<Response<Void>>

    @POST("/user/room/{room_id}/purchase")
    fun createPurchase(@Path("room_id") roomId: Long, @Body purchaseBody: PurchasesBody):
        Single<Response<Void>>

    @POST("/user/room/{room_id}/code")
    fun getRoomCode(@Path("room_id") roomId: Long): Single<Response<RoomCodeResponse>>

    @DELETE("/user/room/{room_id}")
    fun deleteRoom(@Path("room_id") roomId: Long): Single<Response<Void>>

    @DELETE("/user/room/{room_id}/purchase/{purchase_id}")
    fun deletePurchase(
        @Path("room_id") roomId: Long,
        @Path("purchase_id") purchaseId: Long
    ): Single<Response<Void>>

    @PUT("/user/room/{room_id}/purchase/{purchase_id}/join")
    fun setPurchaseJoin(
        @Path("room_id") roomId: Long,
        @Path("purchase_id") purchaseId: Long,
        @Body setPurchaseJoinBody: SetPurchaseJoinBody
    ): Single<Response<Void>>
}
