package com.example.departmentproject

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Room(
    @SerializedName("room_id") @Expose val room_id: Int,
    @SerializedName("room_number") @Expose val room_number: String,
    @SerializedName("status") @Expose val status: String,
    @SerializedName("building_id") @Expose val building_id: Int,
    @SerializedName("room_type_id") @Expose val room_type_id: Int,
    @SerializedName("picture") @Expose val picture: String? = null,
    @SerializedName("owner_id") @Expose val owner_id: Int? = null,
    @SerializedName("building_name") @Expose val building_name: String? = null
)

data class Building(
    @SerializedName("building_id") val building_id: Int,
    @SerializedName("building_name") val building_name: String,
    @SerializedName("owner_id") val owner_id: Int
)

data class RoomType(
    @SerializedName("room_type_id") val room_type_id: Int,
    @SerializedName("type_name") val type_name: String,
    @SerializedName("price") val price: Double,
    @SerializedName("owner_id") val owner_id: Int
)
