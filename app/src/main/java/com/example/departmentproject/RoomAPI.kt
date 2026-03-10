package com.example.departmentproject

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface RoomAPI {
    @GET("room")
    suspend fun retrieveRoom(
        @Query("owner_id") ownerId: Int
    ): List<Room>

    @GET("buildings")
    suspend fun getBuildings(
        @Query("owner_id") ownerId: Int
    ): List<Building>

    @GET("roomTypes")
    suspend fun getRoomTypes(
        @Query("owner_id") ownerId: Int
    ): List<RoomType>

    @POST("insertRoom")
    suspend fun insertRoom(
        @Body room: Room
    ): Response<ApiResponse>

    @PUT("room/{id}")
    suspend fun updateRoomStatus(
        @Path("id") id: Int,
        @Body statusMap: Map<String, String>
    ): Response<ApiResponse>

    @PUT("room/{id}")
    suspend fun updateRoom(
        @Path("id") id: Int,
        @Body room: Room
    ): Response<ApiResponse>

    @DELETE("room/{id}")
    suspend fun deleteRoom(
        @Path("id") id: Int
    ): Response<ApiResponse>

    @GET("owner_id/{user_id}")
    suspend fun getOwnerId(
        @Path("user_id") userId: Int
    ): Response<OwnerIdResponse>

    @Multipart
    @POST("/uploadSlip")
    suspend fun uploadSlip(

        @Part("bill_id") billId: RequestBody,

        @Part slip: MultipartBody.Part

    ): Response<ApiResponse>
    @GET("bill/room/{room_id}")
    suspend fun getLatestBillByRoom(
        @Path("room_id") roomId: Int
    ): Response<BillResponse> // สร้าง Data Class รองรับผลลัพธ์

}

data class BillResponse(
    val bill_id: Int,
    val user_id: Int,
    val total_amount: Double,
    val status: String
)


data class OwnerIdResponse(
    val owner_id: Int?
)
