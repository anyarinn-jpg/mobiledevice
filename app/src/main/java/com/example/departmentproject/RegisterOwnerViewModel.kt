package com.example.departmentproject

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RegisterOwnerViewModel : ViewModel() {

    var dormName by mutableStateOf("")
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var address by mutableStateOf("")
    var phone by mutableStateOf("")

    val buildings = mutableStateListOf<BuildingInput>()
    val roomTypes = mutableStateListOf<RoomTypeInput>()

    fun addBuilding() {
        buildings.add(BuildingInput(""))
    }

    fun addRoomType() {
        roomTypes.add(RoomTypeInput("", ""))
    }

    fun register(context: Context) {
        println("Buildings: $buildings")
        println("RoomTypes: $roomTypes")

        viewModelScope.launch {

            try {

                val buildingList = buildings.map {
                    BuildingInput(it.name)
                }

                val roomTypeList = roomTypes.map {
                    RoomTypeInput(it.name, it.price)
                }

                val request = RegisterOwnerRequest(
                    dorm_name = dormName,
                    address = address,
                    phone = phone,
                    username = username,
                    password = password,
                    buildings = buildingList,
                    room_types = roomTypeList
                )
                val response = RetrofitInstance.api.registerOwner(request)
                println("REQUEST: $request")
                println("RESPONSE: ${response.body()}")

                if (response.isSuccessful) {

                    Toast.makeText(
                        context,
                        "Register Success",
                        Toast.LENGTH_LONG
                    ).show()

                }

            } catch (e: Exception) {

                Toast.makeText(
                    context,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()

            }

        }

    }
}