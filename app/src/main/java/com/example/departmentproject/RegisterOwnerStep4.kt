package com.example.departmentproject

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun RegisterOwnerStep4(
    vm: RegisterOwnerViewModel,
    navController: NavHostController
) {

    val context = LocalContext.current

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {

        Text("ตรวจสอบข้อมูล", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(Modifier.padding(16.dp)) {

                Text("หอพัก: ${vm.dormName}")

                Spacer(Modifier.height(10.dp))

                Text("ตึก")

                vm.buildings.forEach {
                    Text("- ${it.name}")
                }

                Spacer(Modifier.height(10.dp))

                Text("ประเภทห้อง")

                vm.roomTypes.forEach {
                    Text("- ${it.name} : ${it.price}")
                }

            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {

                vm.register(context)

                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }

            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6F77CE)
            )
        ) {
            Text("สมัครสมาชิก")
        }
    }
}