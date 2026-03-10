package com.example.departmentproject

data class Tenant(
    val tenant_id: Int,
    val first_name: String,
    val last_name: String,
    val phone: String,
    val email: String,
    val check_in_date: String,
    val check_out_date: String,
    val room_id: Int
)