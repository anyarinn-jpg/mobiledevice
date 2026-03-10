package com.example.departmentproject

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RoomClient {
    private const val BASE_URL = "http://10.0.2.2:3000/"

    val roomAPI: RoomAPI by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RoomAPI::class.java)
    }
}