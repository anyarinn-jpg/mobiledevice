package com.example.departmentproject

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RegisterOwnerStep1(
    vm: RegisterOwnerViewModel,
    next: () -> Unit
) {

    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(70.dp))

        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color(0xFFE8EAFB), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Apartment,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(60.dp)
                    .background(Color(0xFF6F77CE), RoundedCornerShape(20.dp))
                    .padding(14.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        Text("สมัครเจ้าของหอพัก", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text("กรอกข้อมูลหอพัก", color = Color.Gray)

        Spacer(Modifier.height(28.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {

                OutlinedTextField(
                    value = vm.dormName,
                    onValueChange = { vm.dormName = it },
                    label = { Text("ชื่อหอพัก") },
                    leadingIcon = { Icon(Icons.Default.Home, null) },
                    isError = showError && vm.dormName.isBlank(),
                    supportingText = {
                        if (showError && vm.dormName.isBlank()) {
                            Text("กรุณากรอกชื่อหอพัก")
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = vm.address,
                    onValueChange = { vm.address = it },
                    label = { Text("ที่อยู่") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                    isError = showError && vm.address.isBlank(),
                    supportingText = {
                        if (showError && vm.address.isBlank()) {
                            Text("กรุณากรอกที่อยู่")
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = vm.phone,
                    onValueChange = { vm.phone = it },
                    label = { Text("เบอร์โทร") },
                    leadingIcon = { Icon(Icons.Default.Phone, null) },
                    isError = showError && vm.phone.isBlank(),
                    supportingText = {
                        if (showError && vm.phone.isBlank()) {
                            Text("กรุณากรอกเบอร์โทร")
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = vm.username,
                    onValueChange = { vm.username = it },
                    label = { Text("Username") },
                    leadingIcon = { Icon(Icons.Default.Person, null) },
                    isError = showError && vm.username.isBlank(),
                    supportingText = {
                        if (showError && vm.username.isBlank()) {
                            Text("กรุณากรอก username")
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = vm.password,
                    onValueChange = { vm.password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, null) },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = showError && vm.password.isBlank(),
                    supportingText = {
                        if (showError && vm.password.isBlank()) {
                            Text("กรุณากรอกรหัสผ่าน")
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {

                        if (
                            vm.dormName.isBlank() ||
                            vm.address.isBlank() ||
                            vm.phone.isBlank() ||
                            vm.username.isBlank() ||
                            vm.password.isBlank()
                        ) {
                            showError = true
                        } else {
                            next()
                        }

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6F77CE)
                    )
                ) {
                    Text("ถัดไป", fontSize = 16.sp)
                }

            }
        }
    }
}