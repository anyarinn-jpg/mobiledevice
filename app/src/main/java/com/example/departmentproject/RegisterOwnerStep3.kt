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
fun RegisterOwnerStep3(
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

        Text("เพิ่มตึก", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { vm.addBuilding() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6F77CE)
            )
        ) {
            Icon(Icons.Default.Add, null)
            Spacer(Modifier.width(8.dp))
            Text("เพิ่มตึก")
        }

        Spacer(Modifier.height(20.dp))

        vm.buildings.forEachIndexed { index, building ->

            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {

                Column(Modifier.padding(16.dp)) {

                    OutlinedTextField(
                        value = building.name,
                        onValueChange = {
                            vm.buildings[index] =
                                vm.buildings[index].copy(name = it)
                        },
                        label = { Text("ชื่อตึก") },
                        leadingIcon = { Icon(Icons.Default.Apartment, null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (showError) {
            Text(
                "กรุณาเพิ่มตึกและกรอกชื่อให้ครบ",
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {

                if (
                    vm.buildings.isEmpty() ||
                    vm.buildings.any { it.name.isBlank() }
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