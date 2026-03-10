package com.example.departmentproject

data class TenantByRoomResponse(
    val exists: Boolean,
    val tenant: Tenant?
)
