package com.example.departmentproject

data class RegisterOwnerRequest(

    val dorm_name: String,
    val address: String,
    val phone: String,
    val username: String,
    val password: String,
    val buildings: List<BuildingInput>,
    val room_types: List<RoomTypeInput>

)
