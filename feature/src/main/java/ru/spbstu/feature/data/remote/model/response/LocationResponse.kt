package ru.spbstu.feature.data.remote.model.response

import com.google.gson.annotations.SerializedName
import ru.spbstu.feature.domain.model.Shop

data class LocationResponse(
    @SerializedName("lat")
    val latitude: Double,
    @SerializedName("long")
    val longitude: Double,
    @SerializedName("shop_name")
    val name: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("description")
    val description: String
)

fun LocationResponse.toShop() = Shop(
    name = name,
    latitude = latitude,
    longitude = longitude
)