package com.example.departmentproject

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.annotation.DrawableRes
import androidx.navigation.NavHostController

@Composable
fun BillScreen(navController: NavHostController) {

    val context = LocalContext.current
    val sharedPrefs = SharedPreferencesManager(context)

    var bill by remember { mutableStateOf<UtilityBill?>(null) }
    var loading by remember { mutableStateOf(true) }
    var isPaid by remember { mutableStateOf(false) }

    val userId = sharedPrefs.getUserId()

    LaunchedEffect(userId) {

        try {

            val billResponse = RetrofitInstance.api.getBillByUser(userId)

            if (billResponse.isSuccessful) {
                bill = billResponse.body()
            }

            val paymentResponse = RetrofitInstance.api.checkPayment(userId)

            if (paymentResponse.isSuccessful && paymentResponse.body() != null) {
                isPaid = true
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        loading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {

        Spacer(Modifier.height(24.dp))

        when {

            loading -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

            }

            bill == null -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Room $userId",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Dormitory Bill",
                        color = Color.Gray
                    )

                    Spacer(Modifier.height(40.dp))

                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE3F2FD)
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = "✅",
                                fontSize = 40.sp
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = "ยังไม่มีบิลของเดือนนี้",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "หากชำระแล้วถือว่าเรียบร้อย",
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            else -> {

                val data = bill!!

                val billDate = "${data.bill_month}/${data.bill_year}"

                val electricCost = data.electric_unit * 7
                val waterCost = data.water_unit * 15
                val rentCost = data.rent_price

                val totalAmount = electricCost + waterCost + rentCost

                Text(
                    text = "Room ${data.room_id}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Dormitory Bill",
                    color = Color.Gray
                )

                Spacer(Modifier.height(24.dp))

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
                        if (!isPaid) {
                            navController.navigate(
                                "payment_form/$totalAmount/${data.bill_month}/${data.bill_year}"
                            )
                        }
                    },
                    enabled = !isPaid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (isPaid) Color.Gray else Color(0xFF6C73C8)
                    )
                ) {

                    Spacer(Modifier.width(8.dp))

                    Text(
                        if (isPaid) "คุณชำระเรียบร้อยแล้ว"
                        else "แจ้งการชำระเงิน",
                        color = Color.Black
                    )
                }

                Spacer(Modifier.height(24.dp))
            }
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
                    tint = Color.Unspecified,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(title, fontWeight = FontWeight.SemiBold)

                Text(
                    subtitle,
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }

            Text(
                price,
                fontWeight = FontWeight.Bold
            )
        }
    }
}