package com.example.departmentproject

import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    @POST("payment")
    suspend fun createPayment(
        @Body request: PaymentRequest
    ): Response<Unit>

    @Multipart
    @POST("upload-slip")
    suspend fun uploadSlip(
        @Part slip: MultipartBody.Part,
        @Part("amount") amount: RequestBody,
        @Part("month") month: RequestBody,
        @Part("year") year: RequestBody,
        @Part("user_id") userId: RequestBody
    ): Response<Unit>

    @GET("bill/user/{user_id}")
    suspend fun getBillByUser(
        @Path("user_id") userId: Int
    ): Response<UtilityBill>

    @PUT("tenants/{id}")
    suspend fun updateTenant(
        @Path("id") id: Int,
        @Body request: TenantRequest
    ): Response<ApiResponse>

    @GET("tenantByRoom/{room_id}")
    suspend fun getTenantByRoom(
        @Path("room_id") roomId: Int
    ): Response<TenantByRoomResponse>

    @GET("tenantAccount/{tenant_id}")
    suspend fun getTenantAccount(
        @Path("tenant_id") tenantId: Int
    ): Response<TenantAccountResponse>

    @PUT("tenantAccount/{tenant_id}")
    suspend fun updateTenantAccount(
        @Path("tenant_id") tenantId: Int,
        @Body request: UserRequest
    ): Response<ApiResponse>

    @GET("payment/user/{user_id}")
    suspend fun checkPayment(
        @Path("user_id") userId: Int
    ): Response<Payment>



}