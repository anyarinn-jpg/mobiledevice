package com.example.departmentproject

data class UserRequest(
    val username: String,
    val password: String,
    val role: String,
    val tenant_id: Int
)
