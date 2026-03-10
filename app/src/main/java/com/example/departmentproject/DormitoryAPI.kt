package com.example.departmentproject

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface DormitoryAPI {

    // ─────────────────────────────
    // Login
    // ─────────────────────────────
    @POST("login")
    suspend fun login(
        @Body data: Map<String, String>
    ): Response<LoginClass>


    // ─────────────────────────────
    // Get User Profile
    // ─────────────────────────────
    @GET("search/{id}")
    suspend fun getProfile(
        @Path("id") id: Int
    ): Response<UserProfileClass>


    // ─────────────────────────────
    // Dashboard
    // ─────────────────────────────
    @GET("dashboard")
    suspend fun getDashboard(): Response<DashboardResponse>


    // ─────────────────────────────
    // Update User
    // ─────────────────────────────
    @PUT("updateUser/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body data: Map<String, String>
    ): Response<ApiResponse>


    // ─────────────────────────────
    // Update Bill Status
    // ─────────────────────────────
    @PUT("/bill/status")
    suspend fun updateBillStatus(
        @Body body: Map<String, String>
    ): Response<Unit>

    @POST("register-Owner")
    suspend fun registerOwner(
        @Body request: RegisterOwnerRequest
    ): ApiResponse
}
