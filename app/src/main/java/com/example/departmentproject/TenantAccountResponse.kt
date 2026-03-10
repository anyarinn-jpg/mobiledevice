package com.example.departmentproject

data class TenantAccountResponse(
    val exists: Boolean,
    val user_id: Int?,
    val username: String?,
    val password: String?
)
