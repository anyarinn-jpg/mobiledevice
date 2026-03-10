package com.example.departmentproject

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenantFormScreen(
    navController: NavHostController,
    roomId: Int
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // สีม่วงตาม Reference
    val primaryPurple = Color(0xFF7B7FDB)

    var tenantId by remember { mutableStateOf(0) }
    var isEditMode by remember { mutableStateOf(false) }

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var checkIn by remember { mutableStateOf("") }
    var checkOut by remember { mutableStateOf("") }

    // Error states สำหรับแจ้งเตือนแบบเดิม
    var firstNameError by remember { mutableStateOf(false) }
    var lastNameError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var checkInError by remember { mutableStateOf(false) }
    var checkOutError by remember { mutableStateOf(false) }

    // Logic โหลดข้อมูล (คงเดิม)
    LaunchedEffect(roomId) {
        try {
            val response = RetrofitInstance.api.getTenantByRoom(roomId)
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null && data.exists) {
                    val tenant = data.tenant ?: return@LaunchedEffect
                    tenantId = tenant.tenant_id
                    firstName = tenant.first_name
                    lastName = tenant.last_name
                    phone = tenant.phone
                    email = tenant.email
                    checkIn = tenant.check_in_date.substring(0, 10)
                    checkOut = tenant.check_out_date.substring(0, 10)
                    isEditMode = true
                }
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(context, { _, y, m, d -> onDateSelected("$y-${m + 1}-$d") },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "แก้ไขผู้เช่า" else "เพิ่มผู้เช่า", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            // ชื่อ
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it; firstNameError = it.isEmpty() },
                label = { Text("ชื่อ") },
                leadingIcon = { Icon(Icons.Default.Badge, null) },
                isError = firstNameError,
                supportingText = { if (firstNameError) Text("กรุณากรอกชื่อ") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryPurple, focusedLabelColor = primaryPurple)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // นามสกุล
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it; lastNameError = it.isEmpty() },
                label = { Text("นามสกุล") },
                leadingIcon = { Icon(Icons.Default.Badge, null) },
                isError = lastNameError,
                supportingText = { if (lastNameError) Text("กรุณากรอกนามสกุล") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryPurple, focusedLabelColor = primaryPurple)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // เบอร์โทร
            OutlinedTextField(
                value = phone,
                onValueChange = {
                    if (it.length <= 10 && it.all { c -> c.isDigit() }) {
                        phone = it
                        phoneError = it.length != 10
                    }
                },
                label = { Text("เบอร์โทร") },
                leadingIcon = { Icon(Icons.Default.Phone, null) },
                isError = phoneError,
                supportingText = { if (phoneError) Text("เบอร์โทรต้องมี 10 หลัก") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryPurple, focusedLabelColor = primaryPurple)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = !android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()
                },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, null) },
                isError = emailError,
                supportingText = { if (emailError) Text("รูปแบบ Email ไม่ถูกต้อง") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryPurple, focusedLabelColor = primaryPurple)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // วันที่เข้าพัก
            OutlinedTextField(
                value = checkIn,
                onValueChange = {},
                label = { Text("วันที่เข้าพัก") },
                readOnly = true,
                isError = checkInError,
                supportingText = { if (checkInError) Text("กรุณาเลือกวันที่เข้าพัก") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker { checkIn = it; checkInError = false } }) {
                        Icon(Icons.Default.DateRange, null, tint = primaryPurple)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryPurple)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // วันที่ออก
            OutlinedTextField(
                value = checkOut,
                onValueChange = {},
                label = { Text("วันที่ออก") },
                readOnly = true,
                isError = checkOutError,
                supportingText = { if (checkOutError) Text("กรุณาเลือกวันที่ออก") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker { checkOut = it; checkOutError = false } }) {
                        Icon(Icons.Default.DateRange, null, tint = primaryPurple)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryPurple)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ปุ่มถัดไป
            Button(
                onClick = {
                    // ตรวจสอบข้อมูลก่อนส่ง (คงเดิม)
                    firstNameError = firstName.isEmpty()
                    lastNameError = lastName.isEmpty()
                    phoneError = phone.length != 10
                    emailError = !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                    checkInError = checkIn.isEmpty()
                    checkOutError = checkOut.isEmpty()

                    if (firstNameError || lastNameError || phoneError || emailError || checkInError || checkOutError) {
                        Toast.makeText(context, "กรุณากรอกข้อมูลให้ถูกต้อง", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    scope.launch {
                        try {
                            val response = if (isEditMode) {
                                RetrofitInstance.api.updateTenant(tenantId, TenantRequest(firstName, lastName, phone, email, checkIn, checkOut, roomId))
                            } else {
                                RetrofitInstance.api.insertTenant(TenantRequest(firstName, lastName, phone, email, checkIn, checkOut, roomId))
                            }

                            if (response.isSuccessful) {
                                Toast.makeText(context, if (isEditMode) "แก้ไขสำเร็จ" else "เพิ่มสำเร็จ", Toast.LENGTH_SHORT).show()
                                navController.navigate("tenant_account/$tenantId/$roomId")
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryPurple)
            ) {
                Text("ถัดไป", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}