
package com.example.departmentproject


import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("bill/{room_id}")
    suspend fun getBill(
        @Path("room_id") roomId: Int
    ): Response<UtilityBill>

    @GET("bill/user/{user_id}")
    suspend fun getBillByUser(
        @Path("user_id") userId: Int
    ): Response<UtilityBill>



    @POST("/createTenantAccount")
    suspend fun createTenantAccount(
        @Body request: UserRequest
    ): Response<ApiResponse>

    @GET("/allRooms")
    suspend fun getAllRooms(
        @Query("month") month: String,
        @Query("year") year: String
    ): List<RoomRecord>

    @POST("/addBill")
    suspend fun addBill(
        @Body request: AddBillRequest
    ): Response<Unit>

    @POST("/confirmPayment")
    suspend fun confirmPayment(
        @Body body: Map<String, Int>
    ): Response<Unit>

    @POST("insertRoom")
    suspend fun insertRoom(
        @Body room: Room
    ): Response<ApiResponse>


    // ✅ เพิ่ม
    @GET("/dashboard")
    suspend fun getDashboard(): Response<DashboardResponse>

    // ✅ เพิ่ม
    @POST("/login")
    suspend fun login(@Body body: Map<String, String>): Response<LoginClass>

    // ✅ เพิ่ม (ตรงกับ backend PUT /bill/status)
    @PUT("/bill/status")
    suspend fun updateBillStatus(
        @Body body: Map<String, String>
    ): Response<Unit>


    @PUT("/updateUser/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body body: Map<String, String>
    ): Response<Unit>

    @POST("register-owner")
    suspend fun registerOwner(
        @Body request: RegisterOwnerRequest
    ): Response<ApiResponse>

    @GET("buildings")
    suspend fun getBuildings(
        @Query("owner_id") ownerId: Int
    ): List<Building>

    @POST("/tenants")
    suspend fun insertTenant(
        @Body request: TenantRequest
    ): Response<TenantResponse>


}

