package com.example.departmentproject

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: DormitoryViewModel
) {

    val context = LocalContext.current
    val sharedPrefs = SharedPreferencesManager(context)

    // 🔹 STATE
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val loginResult = viewModel.loginResult


    // 🔹 SIDE EFFECT → ฟังผล login
    LaunchedEffect(loginResult) {

        loginResult?.let {

            if (!it.error && it.user_id != null) {

                Toast.makeText(context, "Login successful. User ID: ${it.user_id}", Toast.LENGTH_LONG).show()
                sharedPrefs.saveUserId(it.user_id)
                sharedPrefs.saveLoginStatus(
                    isLoggedIn = true,
                    stdId = it.user_id.toString(),
                    role = it.role ?: ""
                )

                // ✅ บันทึก owner_id
                sharedPrefs.saveOwnerId(it.owner_id ?: 0)

                println("OWNER ID FROM API = ${it.owner_id}")

                if (it.role.equals("admin", true)) {
                    navController.navigate(Screen.AdminDashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                } else {
                    navController.navigate(Screen.Finance.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            } else {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }

            viewModel.resetLoginResult()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(80.dp))

        // 🔷 Logo
        Box(
            modifier = Modifier
                .size(110.dp)
                .background(Color(0xFFE8EAFB), RoundedCornerShape(28.dp)),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                Icons.Default.Apartment,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(70.dp)
                    .background(
                        Color(0xFF6F77CE),
                        RoundedCornerShape(22.dp)
                    )
                    .padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text("ยินดีต้อนรับ", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("ระบบการจัดการหอพัก", color = Color.Gray)

        Spacer(modifier = Modifier.height(32.dp))

        // 🔷 Username
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("ชื่อผู้ใช้งาน") },
            leadingIcon = { Icon(Icons.Default.Person, null) },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔷 Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("รหัสผ่าน") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            },
            visualTransformation =
                if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.login(username, password)},
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6F77CE)
            )
        ) {
            Text("เข้าสู่ระบบ", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                "ยังไม่มีบัญชี? ",
                color = Color.Gray
            )

            TextButton(
                onClick = {
                    navController.navigate("registerOwner")
                }
            ) {
                Text(
                    "สมัครสมาชิกสำหรับเจ้าของหอ",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6F77CE)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            "พบปัญหาการใช้งาน? ติดต่อฝ่ายเทคนิค",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}