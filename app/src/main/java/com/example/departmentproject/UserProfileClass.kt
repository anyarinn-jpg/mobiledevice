package com.example.departmentproject

import com.google.gson.annotations.SerializedName

data class UserProfileClass(
    @SerializedName("error") val error: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("user_id") val user_id: Int?,
    @SerializedName("username") val username: String?,
    @SerializedName("role") val role: String?
)
