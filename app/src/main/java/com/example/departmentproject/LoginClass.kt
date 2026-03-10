package com.example.departmentproject

import com.google.gson.annotations.SerializedName

data class LoginClass(

    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("user_id")
    val user_id: Int?,

    @SerializedName("role")
    val role: String?,

    @SerializedName("owner_id")
    val owner_id: Int?
)