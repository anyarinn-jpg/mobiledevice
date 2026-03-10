package com.example.departmentproject

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTenantScreen(
    onBack: () -> Unit
) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var building by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color(0xFFF5F6FA),
        topBar = {
            Column {

                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "แก้ไขข้อมูลผู้เช่า",
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    )
                )

                // ✅ เส้นคั่นด้านล่าง
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color(0xFFE0E0E0)
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            SectionHeader("ข้อมูลส่วนตัว", Icons.Default.Person)

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    RoundedTextField("ชื่อ - นามสกุล", name) { name = it }
                    RoundedTextField("อีเมล", email) { email = it }
                    RoundedTextField("เบอร์โทรศัพท์", phone) { phone = it }
                }
            }

            SectionHeader("การมอบหมายห้องพัก", Icons.Default.Home)

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Row(
                    Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(Modifier.weight(1f)) {
                        RoundedTextField("อาคาร / หอพัก", building) { building = it }
                    }
                    Box(Modifier.weight(1f)) {
                        RoundedTextField("เลขที่ห้อง", room) { room = it }
                    }
                }
            }

            SectionHeader("รายละเอียดสัญญา", Icons.Default.Description)

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Row(
                    Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(Modifier.weight(1f)) {
                        RoundedTextField("วันเริ่มสัญญา", startDate) { startDate = it }
                    }
                    Box(Modifier.weight(1f)) {
                        RoundedTextField("วันสิ้นสุดสัญญา", endDate) { endDate = it }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            ActionButtons()
            Spacer(modifier = Modifier.height(0.dp))
        }
    }
}
