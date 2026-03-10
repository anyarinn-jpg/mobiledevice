package com.example.departmentproject

import com.google.gson.annotations.SerializedName
data class RoomRecord(
    @SerializedName("room_id") val roomId: Int,
    @SerializedName("user_id") val userId: Int?,
    @SerializedName("owner_id") val ownerId: Int?, // เพิ่มตัวนี้เพื่อให้ตอนดึงห้องมา มี ID เจ้าของติดมาด้วย
    @SerializedName("room_number") val roomNumber: String,
    @SerializedName("building_id") val buildingId: Int,
    @SerializedName("status") val status: String,

    @SerializedName("current_elec") val currentElec: Double?,
    @SerializedName("previous_water") val previousWater: Double?,
    @SerializedName("current_water") val currentWater: Double?,
    @SerializedName("previous_elec") val previousElec: Double?,

    @SerializedName("bill_month") val billMonth: String?,
    @SerializedName("bill_year") val billYear: String?,

    @SerializedName("last_bill_amount") val lastBillAmount: Double?,
    @SerializedName("bill_status") val billStatus: String?,
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("last_name") val lastName: String?
)