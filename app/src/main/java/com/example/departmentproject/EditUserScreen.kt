package com.example.departmentproject

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun EditUserScreen(
    userId: Int,
    vm: DormitoryViewModel,
    navController: NavHostController
) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(Modifier.padding(16.dp)) {

        Text("แก้ไขบัญชีผู้ใช้", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") }
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password (เว้นว่างถ้าไม่แก้)") }
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                vm.updateUser(userId, username, password)
                navController.popBackStack()
            }
        ) {
            Text("บันทึก")
        }
    }
}