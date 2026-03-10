package com.example.departmentproject

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @FormUrlEncoded
    @POST("insertRoom")
    suspend fun insertRoom(
        @Field("room_number") room_number: String,
        @Field("status") status: String,
        @Field("building_id") building_id: Int,
        @Field("room_type_id") room_type_id: Int,
        @Field("owner_id") owner_id: Int,
        @Field("picture") picture: String?
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
}

data class OwnerIdResponse(
    val owner_id: Int?
)
