package com.example.departmentproject

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RegisterOwnerStep2(
    vm: RegisterOwnerViewModel,
    next: () -> Unit
) {

    var showError by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {

        Text("เพิ่มประเภทห้อง", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { vm.addRoomType() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6F77CE)
            )
        ) {
            Icon(Icons.Default.Add, null)
            Spacer(Modifier.width(8.dp))
            Text("เพิ่มประเภทห้อง")
        }

        Spacer(Modifier.height(20.dp))

        vm.roomTypes.forEachIndexed { index, room ->

            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {

                Column(Modifier.padding(16.dp)) {

                    OutlinedTextField(
                        value = room.name,
                        onValueChange = {
                            vm.roomTypes[index] =
                                vm.roomTypes[index].copy(name = it)
                        },
                        label = { Text("ชื่อห้อง") },
                        leadingIcon = { Icon(Icons.Default.Bed, null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = room.price,
                        onValueChange = {
                            vm.roomTypes[index] =
                                vm.roomTypes[index].copy(price = it)
                        },
                        label = { Text("ราคา") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (showError) {
            Text(
                "กรุณาเพิ่มประเภทห้องและกรอกข้อมูลให้ครบ",
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {

                if (
                    vm.roomTypes.isEmpty() ||
                    vm.roomTypes.any { it.name.isBlank() || it.price.isBlank() }
                ) {
                    showError = true
                } else {
                    next()
                }

            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6F77CE)
            )
        ) {
            Text("ถัดไป")
        }
    }
}