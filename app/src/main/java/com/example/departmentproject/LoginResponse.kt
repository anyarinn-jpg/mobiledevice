package com.example.departmentproject

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val user_id: Int,
    val username: String,
    val role: String,
    val owner_id: Int
)