package com.example.departmentproject

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.Calendar

// --- 1. Helper Function ---
fun getMonthName(month: String): String {
    val months = listOf(
        "มกราคม", "กุมภาพันธ์", "มีนาคม", "เมษายน", "พฤษภาคม", "มิถุนายน",
        "กรกฎาคม", "สิงหาคม", "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"
    )
    return try {
        months[(month.toIntOrNull() ?: 1) - 1]
    } catch (e: Exception) {
        month
    }
}

// เพิ่ม Helper สำหรับชื่อเดือนย่อ (ใช้ใน Grid)
fun getShortMonthName(month: String): String {
    val months = listOf(
        "ม.ค.", "ก.พ.", "มี.ค.", "เม.ย.", "พ.ค.", "มิ.ย.",
        "ก.ค.", "ส.ค.", "ก.ย.", "ต.ค.", "พ.ย.", "ธ.ค."
    )
    return try {
        months[(month.toIntOrNull() ?: 1) - 1]
    } catch (e: Exception) {
        month
    }
}

data class AddBillRequest(
    val room_id: Int,
    val bill_month: String,
    val bill_year: String,
    val water_unit: Double,
    val electric_unit: Double,
    val total_amount: Double,
    val status: String
)

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:3000"
    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

// --- 4. Main Screen ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeterScreen(onBack: () -> Unit) {
    var buildingExpanded by remember { mutableStateOf(false) }
    var buildingList by remember { mutableStateOf<List<Building>>(emptyList()) }
    val calendar = Calendar.getInstance()
    val currentMonthStr = (calendar.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
    val currentYearStr = calendar.get(Calendar.YEAR).toString()
    val context = LocalContext.current
    val sharedPrefs = SharedPreferencesManager(context)
    val ownerId = sharedPrefs.getOwnerId()

    var roomList by remember { mutableStateOf<List<RoomRecord>>(emptyList()) }
    var selectedBuildingId by remember { mutableStateOf(1) }
    var selectedMonth by remember { mutableStateOf(currentMonthStr) }
    var selectedYear by remember { mutableStateOf(currentYearStr) }
    var showMonthPicker by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var editingRoom by remember { mutableStateOf<RoomRecord?>(null) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        try {

            buildingList = RetrofitClient.instance.getBuildings(ownerId)

            if (buildingList.isNotEmpty()) {
                selectedBuildingId = buildingList.first().building_id
            }

        } catch (e: Exception) {
            Log.e("API", "Load building error: ${e.message}")
        }
    }

    fun refreshData() {
        scope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.getAllRooms(selectedMonth, selectedYear)
                roomList = response
                    .filter { it.status.trim().lowercase() == "ไม่ว่าง" }
                    .distinctBy { it.roomId } // ป้องกันห้องเบิ้ลถ้า API ส่งมาซ้ำ
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error: ${e.message}")
            }
            isLoading = false
        }
    }

    LaunchedEffect(selectedMonth, selectedYear) {
        refreshData()
    }

    if (editingRoom != null) {
        EditBillScreen(
            room = editingRoom!!,
            currentMonth = selectedMonth,
            currentYear = selectedYear,
            onBack = {
                editingRoom = null
                refreshData()
            }
        )
    } else {
        Scaffold(
            containerColor = Color(0xFFF8F9FF),
            topBar = {
                Surface(shadowElevation = 2.dp) {
                    Column(modifier = Modifier.background(Color.White)) {
                        TopAppBar(
                            title = { Text("จดบันทึกมิเตอร์", fontWeight = FontWeight.Bold, color = Color(0xFF1A237E)) },
                            navigationIcon = {
                                IconButton(onClick = onBack) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF1A237E))
                                }
                            }
                        )

                        Surface(
                            onClick = { showMonthPicker = true },
                            color = Color(0xFFE8EAF6),
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text("แสดงข้อมูลบิลเดือน (แตะเพื่อเปลี่ยน)", fontSize = 12.sp, color = Color.Gray)
                                    Text("${getMonthName(selectedMonth)} $selectedYear", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3F51B5))
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(Icons.Default.DateRange, contentDescription = null, tint = Color(0xFF3F51B5))
                            }
                        }

                        ExposedDropdownMenuBox(
                            expanded = buildingExpanded,
                            onExpandedChange = { buildingExpanded = !buildingExpanded },
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                        ) {

                            val selectedBuilding =
                                buildingList.find { it.building_id == selectedBuildingId }

                            OutlinedTextField(
                                value = selectedBuilding?.building_name ?: "เลือกอาคาร",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("อาคาร") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = buildingExpanded)
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = buildingExpanded,
                                onDismissRequest = { buildingExpanded = false }
                            ) {
                                buildingList.forEach { building ->
                                    DropdownMenuItem(
                                        text = { Text(building.building_name) },
                                        onClick = {
                                            selectedBuildingId = building.building_id
                                            buildingExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    val filteredRooms = roomList.filter { it.buildingId == selectedBuildingId }

                    if (filteredRooms.isEmpty()) {
                        Text("ไม่พบข้อมูลห้องพัก", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
                    } else {
                        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            item { Text("รายการห้องพัก (${filteredRooms.size})", fontWeight = FontWeight.Bold, color = Color.Gray) }
                            items(filteredRooms) { room ->
                                RoomReferenceCard(
                                    room = room,
                                    onEditClick = { editingRoom = room },
                                    onConfirmPayment = {
                                        scope.launch {
                                            try {
                                                val response = RetrofitClient.instance.confirmPayment(mapOf("room_id" to room.roomId))
                                                if (response.isSuccessful) refreshData()
                                            } catch (e: Exception) { Log.e("API", "${e.message}") }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // เรียกใช้ Component ใหม่
    if (showMonthPicker) {
        MonthYearPickerDialog(
            currentMonth = selectedMonth,
            currentYear = selectedYear,
            onConfirm = { newMonth, newYear ->
                selectedMonth = newMonth
                selectedYear = newYear
                showMonthPicker = false
            },
            onDismiss = { showMonthPicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBillScreen(room: RoomRecord, currentMonth: String, currentYear: String, onBack: () -> Unit) {
    var newElec by remember { mutableStateOf("") }
    var newWater by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val prevElec = room.previousElec ?: 0.0
    val prevWater = room.previousWater ?: 0.0

    val elecDiff = (newElec.toDoubleOrNull() ?: 0.0) - prevElec
    val waterDiff = (newWater.toDoubleOrNull() ?: 0.0) - prevWater
    val calculatedTotal = if (elecDiff >= 0 && waterDiff >= 0) (elecDiff * 7.0) + (waterDiff * 20.0) + 150.0 else 0.0

    Scaffold(
        containerColor = Color(0xFFF8F9FF),
        topBar = {
            TopAppBar(
                title = { Text("บันทึกเดือน ${getMonthName(currentMonth)} ห้อง ${room.roomNumber}", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "กลับ", tint = Color(0xFF1A237E))
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp)) {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6))) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("มิเตอร์อ้างอิงตั้งต้น", fontWeight = FontWeight.Bold, color = Color(0xFF3F51B5))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("ไฟ: $prevElec u.", fontSize = 14.sp)
                        Text("น้ำ: $prevWater u.", fontSize = 14.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(value = newElec, onValueChange = { newElec = it }, label = { Text("เลขมิเตอร์ไฟที่จดได้") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), shape = RoundedCornerShape(12.dp))
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = newWater, onValueChange = { newWater = it }, label = { Text("เลขมิเตอร์น้ำที่จดได้") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), shape = RoundedCornerShape(12.dp))

            if (calculatedTotal > 0) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("ยอดคำนวณ: ฿${String.format("%.2f", calculatedTotal)}", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = Color(0xFF2E7D32))
            }

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    scope.launch {
                        try {
                            val requestData = AddBillRequest(
                                room_id = room.roomId,
                                bill_month = currentMonth,
                                bill_year = currentYear,
                                water_unit = newWater.toDoubleOrNull() ?: 0.0,
                                electric_unit = newElec.toDoubleOrNull() ?: 0.0,
                                total_amount = calculatedTotal,
                                status = "pending"
                            )
                            RetrofitClient.instance.addBill(requestData)
                            onBack()
                        } catch (e: Exception) { Log.e("API", "Error: ${e.message}") }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = newElec.isNotEmpty() && newWater.isNotEmpty(),
                shape = RoundedCornerShape(16.dp)
            ) { Text("บันทึกบิลเดือน ${getMonthName(currentMonth)}") }
        }
    }
}

@Composable
fun RoomReferenceCard(
    room: RoomRecord,
    onEditClick: () -> Unit,
    onConfirmPayment: () -> Unit
) {
    val hasBill = room.billMonth != null
    val displayAmount = room.lastBillAmount ?: 0.0
    val elecUsed = if (hasBill) ((room.currentElec ?: 0.0) - (room.previousElec ?: 0.0)) else 0.0
    val waterUsed = if (hasBill) ((room.currentWater ?: 0.0) - (room.previousWater ?: 0.0)) else 0.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(modifier = Modifier.size(45.dp), shape = CircleShape, color = Color(0xFFE8EAF6)) {
                        Box(contentAlignment = Alignment.Center) { Text(room.roomNumber, fontWeight = FontWeight.Bold, color = Color(0xFF3F51B5)) }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("ห้อง ${room.roomNumber}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(if (!room.firstName.isNullOrEmpty()) "${room.firstName} ${room.lastName ?: ""}" else "ไม่มีข้อมูล", fontSize = 13.sp, color = Color.Gray)
                    }
                }
                Text(
                    "฿${String.format("%.2f", displayAmount)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (!hasBill) Color.Gray else if (room.billStatus == "pending") Color(0xFFFBC02D) else Color(0xFF3F51B5)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Bolt, null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                    Text(" ไฟ: ${String.format("%.2f", if(elecUsed > 0) elecUsed else 0.0)} u.", fontSize = 12.sp)
                }
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WaterDrop, null, tint = Color(0xFF2196F3), modifier = Modifier.size(14.dp))
                    Text(" น้ำ: ${String.format("%.2f", if(waterUsed > 0) waterUsed else 0.0)} u.", fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF5F5F5))
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                if (room.billStatus == "pending") {
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFFF9C4)) {
                        Text("รอดำเนินการ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF57F17), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                } else if (room.billStatus == "paid" || room.billStatus == "completed") {
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFE8F5E9)) {
                        Text("ชำระแล้ว", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                } else {
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFF5F5F5)) {
                        Text("ยังไม่จดมิเตอร์", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                if (room.billStatus == "pending") {
                    Button(onClick = onConfirmPayment, modifier = Modifier.height(40.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(horizontal = 12.dp)) {
                        Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("จ่ายแล้ว", fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Button(onClick = onEditClick, modifier = Modifier.height(40.dp), shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(horizontal = 12.dp)) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("บันทึก", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun BuildingTab(label: String, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(42.dp),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) Color(0xFF3F51B5) else Color(0xFFF0F0F0)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(label, color = if (isSelected) Color.White else Color.Gray, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

// --- Component ใหม่สำหรับเลือกเดือนและปี ---
@Composable
fun MonthYearPickerDialog(
    currentMonth: String,
    currentYear: String,
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var tempMonth by remember { mutableStateOf(currentMonth) }
    var tempYear by remember { mutableStateOf(currentYear.toIntOrNull() ?: Calendar.getInstance().get(Calendar.YEAR)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        title = {
            // ส่วนควบคุมปี
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { tempYear-- }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "ปีที่แล้ว", tint = Color(0xFF3F51B5))
                }
                Text(text = "ปี $tempYear", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color(0xFF1A237E))
                IconButton(onClick = { tempYear++ }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "ปีถัดไป", tint = Color(0xFF3F51B5))
                }
            }
        },
        text = {
            // ส่วนควบคุมเดือน (Grid 3x4)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                for (row in 0 until 4) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        for (col in 0 until 3) {
                            val monthNum = row * 3 + col + 1
                            val monthStr = monthNum.toString().padStart(2, '0')
                            val isSelected = tempMonth == monthStr

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(0xFF3F51B5) else Color(0xFFF5F5F5),
                                onClick = { tempMonth = monthStr }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = getShortMonthName(monthStr),
                                        color = if (isSelected) Color.White else Color.DarkGray,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(tempMonth, tempYear.toString()) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("ตกลง", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ยกเลิก", color = Color.Gray, fontWeight = FontWeight.Bold)
            }
        }
    )
}
