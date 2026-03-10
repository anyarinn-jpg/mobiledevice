package com.example.departmentproject

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenantAccountScreen(
    navController: NavHostController,
    tenantId: Int,
    roomId: Int
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // --- สถานะสำหรับเปิด/ปิดตาแสดงรหัสผ่าน ---
    var passwordVisible by remember { mutableStateOf(false) }

    var isEditMode by remember { mutableStateOf(false) }

    // สีม่วงตาม Reference
    val primaryPurple = Color(0xFF7B7FDB)

    LaunchedEffect(tenantId) {
        try {
            val response = RetrofitInstance.api.getTenantAccount(tenantId)
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null && data.exists) {
                    username = data.username ?: ""
                    password = ""
                    isEditMode = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("สร้างบัญชีผู้เช่า", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .fillMaxSize()
        ) {
            // --- ช่องกรอก Username ---
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("ชื่อผู้ใช้งาน") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryPurple,
                    focusedLabelColor = primaryPurple,
                    unfocusedBorderColor = Color.LightGray
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- ช่องกรอก Password (กดตาแล้วโชว์รหัส) ---
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("รหัสผ่าน") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray) },
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null, tint = Color.Gray)
                    }
                },
                // สลับการแสดงผลระหว่างจุดกับตัวอักษรปกติ
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryPurple,
                    focusedLabelColor = primaryPurple,
                    unfocusedBorderColor = Color.LightGray
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- ปุ่มบันทึก ---
            Button(
                onClick = {
                    scope.launch {
                        try {
                            val response = if (isEditMode) {
                                RetrofitInstance.api.updateTenantAccount(
                                    tenantId,
                                    UserRequest(username, password, "tenant", tenantId)
                                )
                            } else {
                                RetrofitInstance.api.createTenantAccount(
                                    UserRequest(username, password, "tenant", tenantId)
                                )
                            }

                            if (response.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    if (isEditMode) "แก้ไขบัญชีสำเร็จ" else "สร้างบัญชีสำเร็จ",
                                    Toast.LENGTH_LONG
                                ).show()

                                navController.navigate(Screen.AdminDashboard.route) {
                                    popUpTo(Screen.AdminDashboard.route) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryPurple)
            ) {
                Text(
                    text = if (isEditMode) "บันทึกการแก้ไข" else "สร้างบัญชี",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}