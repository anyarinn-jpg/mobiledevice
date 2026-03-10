package com.example.departmentproject

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentFormScreen(
    amountFromBill: String,
    month: String,
    year: String,
    onNext: () -> Unit,
    onCancel: () -> Unit
) {

    var amount by remember { mutableStateOf(amountFromBill) }
    val billDate = "$month/$year"

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            TopAppBar(
                title = { Text("เลือกการชำระเงิน") }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            // 🔵 ยอดรวม
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFEFF6FF)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("ยอดที่ต้องชำระ", color = Color.Gray)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "$amountFromBill บาท",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFF1D4ED8)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("รอบบิล: $billDate", color = Color.Gray)
                }
            }

            Spacer(Modifier.height(24.dp))

            Text("รายละเอียดการชำระ",
                style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = billDate,
                onValueChange = {},
                enabled = false,
                label = { Text("รอบบิล") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("จำนวนเงิน (บาท)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            // 🔥 แนบสลิปกลับมาแล้ว
            Text(
                "แนบหลักฐานการโอน",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .border(
                        1.dp,
                        Color(0xFFCBD5E1),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("แตะเพื่ออัปโหลดสลิป", color = Color.Gray)
                    Spacer(Modifier.height(4.dp))
                    Text("รองรับ JPG, PNG", color = Color.LightGray)
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("ยืนยันการชำระ")
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