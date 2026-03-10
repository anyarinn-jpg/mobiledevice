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
fun TenantAccountScreen(
    navController: NavHostController,
    tenantId: Int,
    roomId: Int
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("สร้างบัญชีผู้เช่า") })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
        ) {

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username ห้อง") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {

                    scope.launch {

                        try {

                            val response =
                                RetrofitInstance.api.createTenantAccount(
                                    UserRequest(
                                        username,
                                        password,
                                        "tenant",
                                        tenantId
                                    )
                                )

                            if (response.isSuccessful) {

                                Toast.makeText(
                                    context,
                                    "สร้างบัญชีสำเร็จ",
                                    Toast.LENGTH_LONG
                                ).show()

                                navController.navigate(Screen.AdminDashboard.route) {
                                    popUpTo(Screen.AdminDashboard.route) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }

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

                Text("สร้างบัญชี")

            }

        }

    }

}