package com.example.departmentproject

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

class RoomViewModel : ViewModel() {
    var roomList = mutableStateListOf<Room>()
        private set

    var buildings = mutableStateListOf<Building>()
        private set

    var roomTypes = mutableStateListOf<RoomType>()
        private set

    var ownerId by mutableIntStateOf(0)

    var searchQuery by mutableStateOf("")

    val filteredRoomList: List<Room>
        get() = if (searchQuery.isEmpty()) roomList else roomList.filter { it.room_number.contains(searchQuery, ignoreCase = true) }

    var totalCount by mutableIntStateOf(0)
    var availableCount by mutableIntStateOf(0)
    var unavailableCount by mutableIntStateOf(0)

    // Image state
    var selectedImageUri by mutableStateOf<Uri?>(null)

    fun initializeOwner(userId: Int, onComplete: (Int) -> Unit = {}) {
        viewModelScope.launch {
            try {
                Log.d("DROPDOWN_DEBUG", "กำลังดึง owner_id สำหรับ user_id: $userId")
                val response = RoomClient.roomAPI.getOwnerId(userId)
                if (response.isSuccessful) {
                    val id = response.body()?.owner_id ?: 0
                    ownerId = id
                    Log.d("DROPDOWN_DEBUG", "ได้ค่า owner_id: $id")

                    // หลังจากได้ ID แล้ว ให้โหลดข้อมูลทันที
                    fetchBuildingsAndRoomTypes(id)
                    onComplete(id)
                } else {
                    Log.e("DROPDOWN_DEBUG", "ดึง owner_id ไม่สำเร็จ: ${response.code()} ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("DROPDOWN_DEBUG", "Error initializeOwner: ${e.message}")
            }
        }
    }

    fun fetchBuildingsAndRoomTypes(oid: Int) {
        if (oid == 0) return
        viewModelScope.launch {
            try {
                Log.d("DROPDOWN_DEBUG", "กำลังดึงอาคารและประเภทห้องสำหรับ owner_id: $oid")

                // 1. ดึงข้อมูลอาคาร
                val bResponse = RoomClient.roomAPI.getBuildings(oid)
                buildings.clear()
                buildings.addAll(bResponse)
                Log.d("DROPDOWN_DEBUG", "โหลดอาคารสำเร็จ: ${bResponse.size} รายการ")

                // 2. ดึงข้อมูลประเภทห้อง
                val rtResponse = RoomClient.roomAPI.getRoomTypes(oid)
                roomTypes.clear()
                roomTypes.addAll(rtResponse)
                Log.d("DROPDOWN_DEBUG", "โหลดประเภทห้องสำเร็จ: ${rtResponse.size} รายการ")

            } catch (e: Exception) {
                Log.e("DROPDOWN_DEBUG", "Fetch Dropdown Error: ${e.message}")
            }
        }
    }

    fun getAllRooms(oid: Int) {
        if (oid == 0) return
        viewModelScope.launch {
            try {
                val response = RoomClient.roomAPI.retrieveRoom(oid)
                roomList.clear()
                roomList.addAll(response)
                updateCounts(oid)
            } catch (e: Exception) {
                Log.e("RoomViewModel", "Get Rooms Error: ${e.message}")
            }
        }
    }

    private fun updateCounts(oid: Int) {
        viewModelScope.launch {
            try {
                val all = RoomClient.roomAPI.retrieveRoom(oid)
                totalCount = all.size
                availableCount = all.count { it.status == "ว่าง" }
                unavailableCount = all.count { it.status == "ไม่ว่าง" }
            } catch (e: Exception) { }
        }
    }

    fun insertRoom(room: Room, context: Context, oid: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = RoomClient.roomAPI.insertRoom(room.copy(owner_id = oid))

                if (response.isSuccessful && response.body()?.error == false) {
                    Toast.makeText(context, "เพิ่มสำเร็จ", Toast.LENGTH_SHORT).show()
                    onSuccess()
                    getAllRooms(oid)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: response.body()?.message ?: "Unknown Error"
                    Log.e("RoomViewModel", "Insert Fail: $errorMsg (Code: ${response.code()})")
                    Toast.makeText(context, "เพิ่มไม่สำเร็จ: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("RoomViewModel", "Insert Error: ${e.message}")
                Toast.makeText(context, "เกิดข้อผิดพลาด: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Form States
    var roomIdText by mutableStateOf("")
    var roomNumberText by mutableStateOf("")
    var statusText by mutableStateOf("ว่าง")
    var selectedBuildingId by mutableIntStateOf(0)
    var selectedRoomTypeId by mutableIntStateOf(0)
    var pictureUrlText by mutableStateOf("")

    fun setRoomForEdit(room: Room) {
        roomIdText = room.room_id.toString()
        roomNumberText = room.room_number
        statusText = room.status
        selectedBuildingId = room.building_id
        selectedRoomTypeId = room.room_type_id
        pictureUrlText = room.picture ?: ""
        selectedImageUri = null
    }

    fun clearForm() {
        roomIdText = ""
        roomNumberText = ""
        statusText = "ว่าง"
        selectedBuildingId = 0
        selectedRoomTypeId = 0
        pictureUrlText = ""
        selectedImageUri = null
    }

    fun updateRoom(context: Context, oid: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val room = Room(
                    room_id = roomIdText.toIntOrNull() ?: 0,
                    room_number = roomNumberText,
                    status = statusText,
                    building_id = selectedBuildingId,
                    room_type_id = selectedRoomTypeId,
                    picture = pictureUrlText.ifEmpty { null },
                    owner_id = oid
                )
                val response = RoomClient.roomAPI.updateRoom(room.room_id, room)
                if (response.isSuccessful && response.body()?.error == false) {
                    Toast.makeText(context, "แก้ไขสำเร็จ", Toast.LENGTH_SHORT).show()
                    onSuccess()
                    getAllRooms(oid)
                }
            } catch (e: Exception) { }
        }
    }

    fun toggleRoomStatus(context: Context, room: Room, oid: Int) {
        viewModelScope.launch {
            try {
                val newStatus = if (room.status == "ว่าง") "ไม่ว่าง" else "ว่าง"
                val statusUpdate = mapOf("status" to newStatus)
                val response = RoomClient.roomAPI.updateRoomStatus(room.room_id, statusUpdate)
                if (response.isSuccessful) {
                    getAllRooms(oid)
                }
            } catch (e: Exception) { }
        }
    }

    fun deleteRoom(context: Context, roomId: Int, oid: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = RoomClient.roomAPI.deleteRoom(roomId)
                if (response.isSuccessful && response.body()?.error == false) {
                    Toast.makeText(context, "ลบสำเร็จ", Toast.LENGTH_SHORT).show()
                    onSuccess()
                    getAllRooms(oid)
                }
            } catch (e: Exception) { }
        }
    }

    fun getAvailableRooms(oid: Int) {
        viewModelScope.launch {
            try {
                val all = RoomClient.roomAPI.retrieveRoom(oid)
                roomList.clear()
                roomList.addAll(all.filter { it.status == "ว่าง" })
            } catch (e: Exception) { }
        }
    }

    fun getUnavailableRooms(oid: Int) {
        viewModelScope.launch {
            try {
                val all = RoomClient.roomAPI.retrieveRoom(oid)
                roomList.clear()
                roomList.addAll(all.filter { it.status == "ไม่ว่าง" })
            } catch (e: Exception) { }
        }
    }

    // Helper to convert Uri to Base64
    fun getBase64FromUri(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
            val bytes = outputStream.toByteArray()
            Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (e: Exception) {
            Log.e("RoomViewModel", "Error converting image: ${e.message}")
            null
        }
    }

    fun uploadSlip(
        context: Context,
        billId: Int,
        imageUri: Uri
    ) {

        viewModelScope.launch {

            try {

                val inputStream =
                    context.contentResolver.openInputStream(imageUri)

                val fileBytes = inputStream?.readBytes()

                val requestFile =
                    fileBytes!!.toRequestBody("image/*".toMediaType())

                val slipPart =
                    MultipartBody.Part.createFormData(
                        "slip",
                        "slip.jpg",
                        requestFile
                    )

                val billBody =
                    billId.toString()
                        .toRequestBody("text/plain".toMediaType())

                val response =
                    RoomClient.roomAPI.uploadSlip(
                        billBody,
                        slipPart
                    )

                if (response.isSuccessful) {

                    Toast.makeText(
                        context,
                        "อัปโหลดสลิปสำเร็จ",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    Toast.makeText(
                        context,
                        "อัปโหลดไม่สำเร็จ",
                        Toast.LENGTH_SHORT
                    ).show()

                }

            } catch (e: Exception) {

                Log.e("UPLOAD", e.message ?: "error")

            }

        }
    }
    // เพิ่ม State ใน RoomViewModel
    var currentUserIdByBill by mutableIntStateOf(0)
    var currentBillId by mutableIntStateOf(0)

    fun fetchBillDetails(roomId: Int) {
        viewModelScope.launch {
            try {
                val response = RoomClient.roomAPI.getLatestBillByRoom(roomId)
                if (response.isSuccessful) {
                    val bill = response.body()
                    currentBillId = bill?.bill_id ?: 0
                    currentUserIdByBill = bill?.user_id ?: 0
                    Log.d("BILL_DEBUG", "ดึงข้อมูลบิลสำเร็จ: BillID=$currentBillId, UserID=$currentUserIdByBill")
                }
            } catch (e: Exception) {
                Log.e("BILL_DEBUG", "Error: ${e.message}")
            }
        }
    }
}