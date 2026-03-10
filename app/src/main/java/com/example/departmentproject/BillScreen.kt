package com.example.departmentproject

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



@Composable
fun BillScreen(navController: NavHostController) {

    var bill by remember { mutableStateOf<UtilityBill?>(null) }

    LaunchedEffect(Unit) {
        val response = RetrofitInstance.api.getBill(4)
        if (response.isSuccessful) {
            bill = response.body()
        }
    }

    bill?.let { data ->

        val billDate = "${data.bill_month}/${data.bill_year}"

        // 🔥 คำนวณค่าใช้จ่ายเอง
        val electricCost = data.electric_unit * 7
        val waterCost = data.water_unit * 15
        val rentCost = 3500
        val totalAmount = electricCost + waterCost + rentCost

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Room ${data.room_id} - John Doe",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Ivy Residences - Block A",
                color = Color.Gray
            )

            Spacer(Modifier.height(24.dp))

            // 🔵 Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8EAF6)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text("ยอดรวมที่ต้องชำระ")
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "฿$totalAmount",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5763F3)
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text = "รายการบิล $billDate",
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray
            )

            Spacer(Modifier.height(16.dp))

            BillRow(
                icon = R.drawable.ic_electric,
                iconBg = Color(0xFFFDEEC4),
                title = "ค่าไฟ - $billDate",
                subtitle = "${data.electric_unit} kWh",
                price = "฿$electricCost"
            )

            BillRow(
                icon = R.drawable.ic_water,
                iconBg = Color(0xFFC8E6FD),
                title = "ค่าน้ำ - $billDate",
                subtitle = "${data.water_unit} m³",
                price = "฿$waterCost"
            )

            BillRow(
                icon = R.drawable.ic_home,
                iconBg = Color(0xFF94AAFD),
                title = "ค่าเช่า - $billDate",
                subtitle = "รายเดือน",
                price = "฿$rentCost"
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    navController.navigate(
                        "payment_form/$totalAmount/${data.bill_month}/${data.bill_year}"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6C73C8)
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_money),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("แจ้งการชำระเงิน", color = Color.White)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun BillRow(
    @DrawableRes icon: Int,
    iconBg: Color,
    title: String,
    subtitle: String,
    price: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF4F4F8)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(iconBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = Color.Unspecified, // 🔥 สำคัญ แก้ปัญหาดำทับ
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = Color.Gray, fontSize = 13.sp)
            }

            Text(price, fontWeight = FontWeight.Bold)
        }
    }
}