package com.example.departmentproject

import retrofit2.Response
import retrofit2.http.*

interface TenantApi {

    @GET("tenants/{id}")
    suspend fun getTenant(@Path("id") id: Int): Tenant

    @PUT("tenants/{id}")
    suspend fun updateTenant(
        @Path("id") id: Int,
        @Body tenant: Tenant
    ): Response<Map<String, Any>>

    @DELETE("tenants/{id}")
    suspend fun deleteTenant(
        @Path("id") id: Int
    ): Response<Map<String, Any>>

    @FormUrlEncoded
    @POST("insertTenant")
    suspend fun insertTenant(
        @Field("room_id") room_id: Int,
        @Field("building_id") building_id: Int
    ): Response<ApiResponse>

    @GET("tenant/{id}")
    suspend fun getTenantById(
        @Path("id") id: Int
    ): Tenant

    @POST("registerOwner")
    suspend fun registerOwner(
        @Body request: RegisterOwnerRequest
    ): Response<Unit>

    @POST("registerOwner")
    suspend fun createUser(
        @Body request: UserRequest
    ): Response<Unit>

    @GET("bill/{room_id}")
    suspend fun getBill(
        @Path("room_id") roomId: Int
    ): Response<UtilityBill>

    @POST("tenants")
    suspend fun insertTenant(
        @Body request: TenantRequest
    ): Response<TenantResponse>

    @POST("createTenantAccount")
    suspend fun createTenantAccount(
        @Body request: UserRequest
    ): Response<ApiResponse>




}