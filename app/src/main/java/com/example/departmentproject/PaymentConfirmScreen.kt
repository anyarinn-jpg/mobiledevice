package com.example.departmentproject

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val PrimaryBlue = Color(0xFF2563EB)
private val DarkBlue = Color(0xFF1D4ED8)
private val LightBlue = Color(0xFFEFF6FF)
private val Background = Color(0xFFF8FAFC)
private val BorderGray = Color(0xFFCBD5E1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentConfirmScreen(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("ยืนยันการชำระเงิน") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LightBlue
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("ยอดที่ต้องชำระ", color = Color.Gray)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "3,250.00 บาท",
                        style = MaterialTheme.typography.headlineSmall,
                        color = DarkBlue
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("ครบกำหนด: ก.ย. 2566", color = Color.Gray)
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "รายละเอียดการโอน",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(12.dp))

            ConfirmField("ชื่อผู้โอน", "สมชาย ใจดี (หอ 302)")
            Spacer(Modifier.height(12.dp))
            ConfirmField("วันที่โอนเงิน", "24 ก.ย. 2566")
            Spacer(Modifier.height(12.dp))
            ConfirmField("Ref No.", "110-882110")

            Spacer(Modifier.height(24.dp))

            Text(
                "แนบหลักฐานการโอน",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .border(
                        1.dp,
                        BorderGray,
                        RoundedCornerShape(16.dp)
                    )
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = PrimaryBlue
                    )
                    Spacer(Modifier.height(6.dp))
                    Text("ตัวอย่างสลิปการโอนเงิน", color = Color.Gray)
                    Spacer(Modifier.height(2.dp))
                    Text("รองรับ JPG, PNG", color = Color.LightGray)
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue
                )
            ) {
                Text("ยืนยันการชำระ", color = Color.White)
            }

            Spacer(Modifier.height(12.dp))

            TextButton(
                onClick = onCancel,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("ยกเลิก", color = Color.Gray)
            }
        }
    }
}

@Composable
private fun ConfirmField(label: String, value: String) {
    Column {
        Text(
            label,
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(Modifier.height(4.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                value,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}