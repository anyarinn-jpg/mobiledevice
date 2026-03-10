package com.example.departmentproject

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenantFormScreen(
    navController: NavHostController,
    roomId: Int
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var checkIn by remember { mutableStateOf("") }
    var checkOut by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("เพิ่มผู้เช่า") })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
        ) {

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("ชื่อ") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("นามสกุล") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("เบอร์โทร") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = checkIn,
                onValueChange = { checkIn = it },
                label = { Text("วันที่เข้าพัก") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = checkOut,
                onValueChange = { checkOut = it },
                label = { Text("วันที่ออก") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {

                    scope.launch {

                        try {

                            val response = RetrofitInstance.api.insertTenant(
                                TenantRequest(
                                    first_name = firstName,
                                    last_name = lastName,
                                    phone = phone,
                                    email = email,
                                    check_in_date = checkIn,
                                    check_out_date = checkOut,
                                    room_id = roomId
                                )
                            )
                            if (response.isSuccessful) {

                                val tenantId =
                                    response.body()?.tenant_id ?: 0

                                Toast.makeText(
                                    context,
                                    "เพิ่มผู้เช่าสำเร็จ",
                                    Toast.LENGTH_SHORT
                                ).show()

                                navController.navigate(
                                    "tenant_account/$tenantId"
                                )

                            } else {

                                Toast.makeText(
                                    context,
                                    "Insert tenant failed",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }

                        } catch (e: Exception) {

                            Toast.makeText(
                                context,
                                e.message,
                                Toast.LENGTH_LONG
                            ).show()

                        }

                    }

                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {

                Text("Next")

            }

        }

    }

}