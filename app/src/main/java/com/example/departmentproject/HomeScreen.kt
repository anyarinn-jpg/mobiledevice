package com.example.departmentproject

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import java.text.NumberFormat
import java.util.Locale

// ── สี ────────────────────────────────────────
private val Purple      = Color(0xFF6B6FD4)
private val GradEnd     = Color(0xFF8E92E3)
private val GreenText   = Color(0xFF0BAB72)
private val GreenBg     = Color(0xFFD6F5EB)
private val OrangeText  = Color(0xFFD97706)
private val OrangeBg    = Color(0xFFFEF3C7)
private val BgGray      = Color(0xFFF4F5FB)
private val White       = Color(0xFFFFFFFF)
private val TextMain    = Color(0xFF1C1F35)
private val TextSub     = Color(0xFF9CA3AF)
private val BorderColor = Color(0xFFEEEEEE)

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: DormitoryViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var search by remember { mutableStateOf("") }

    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.getDashboard()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        containerColor = BgGray,
        bottomBar = {
            AdminBottomBar(navController)
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp,
                top = 16.dp, bottom = 8.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── Search Bar ────────────────────────────
            item {
                OutlinedTextField(
                    value         = search,
                    onValueChange = { search = it },
                    placeholder   = {
                        Text("ค้นหาห้องพัก...", color = TextSub, fontSize = 14.sp)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, null, tint = TextSub)
                    },
                    singleLine  = true,
                    shape       = RoundedCornerShape(12.dp),
                    modifier    = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    colors      = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = Purple,
                        unfocusedBorderColor    = Color.Transparent,
                        focusedContainerColor   = White,
                        unfocusedContainerColor = White
                    )
                )
            }

            // ── ยอดรวมเดือนนี้ ─────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(listOf(Purple, GradEnd))
                        )
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                ) {
                    Column {
                        val totalPaidThisMonth = viewModel.dashboardData?.recentPayments
                            ?.filter { it.status == "ชำระแล้ว" }
                            ?.sumOf { it.amount } ?: 0.0

                        Text(
                            "ยอดรวมเดือนนี้",
                            color = Color(0xFFCDD0F0),
                            fontSize = 13.sp
                        )

                        Spacer(Modifier.height(6.dp))

                        if (viewModel.isLoading) {
                            CircularProgressIndicator(
                                color = White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "${fmtMoney(totalPaidThisMonth)} บาท",
                                color = White,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                "เฉพาะบิลที่ชำระแล้ว",
                                color = Color(0xFFCDD0F0),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // ── Section Header ─────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        "รายการล่าสุด",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 17.sp,
                        color      = TextMain
                    )
                    Text(
                        "ดูทั้งหมด",
                        color    = Purple,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable {
                            navController.navigate("bills")
                        }
                    )
                }
            }

            // ── Loading / Error ────────────────────────
            if (viewModel.isLoading) {
                item {
                    Box(
                        Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator(color = Purple) }
                }
            }

            viewModel.errorMessage?.let { msg ->
                item {
                    Card(
                        shape  = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = White)
                    ) {
                        Text(
                            msg,
                            modifier = Modifier.padding(16.dp),
                            color    = Color.Red
                        )
                    }
                }
            }

            // ── Bill List ──────────────────────────────
            val bills = viewModel.dashboardData?.recentPayments
                ?.filter { bill ->
                    search.isBlank() ||
                            bill.roomNumber.contains(search, ignoreCase = true)
                } ?: emptyList()

            items(bills) { bill ->
                BillCard(
                    bill    = bill,
                    // ✅ แก้ไข: กดตรวจสอบ → navigate ไปหน้า detail เพื่อดูสลิปก่อน
                    onInspect = {
                        navController.navigate("bill_detail/${bill.billId}")
                    },
                    onDetail  = {
                        navController.navigate("bill_detail/${bill.billId}")
                    }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
//  BILL CARD
// ─────────────────────────────────────────────
@Composable
private fun BillCard(
    bill: RecentPayment,
    onInspect: () -> Unit,
    onDetail:  () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            StatusChip(bill.status)
            Spacer(Modifier.height(8.dp))

            Text(
                "ห้อง ${bill.roomNumber}",
                fontWeight = FontWeight.Bold,
                fontSize   = 18.sp,
                color      = TextMain
            )

            Text(
                "฿${fmtMoney(bill.amount)}",
                color    = Purple,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
            )

            if (bill.status == "รอตรวจสอบ") {
                Button(
                    onClick  = onInspect,
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape  = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple)
                ) {
                    Icon(
                        Icons.Default.CheckCircle, null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("ตรวจสอบ", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
            } else {
                OutlinedButton(
                    onClick  = onDetail,
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape  = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMain)
                ) {
                    Text("ดูรายละเอียด", fontSize = 14.sp, color = TextMain)
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        Icons.Default.ArrowForward, null,
                        tint = TextSub, modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  STATUS CHIP
// ─────────────────────────────────────────────
@Composable
private fun StatusChip(status: String) {
    val (bg, fg) = when (status) {
        "รอตรวจสอบ"   -> OrangeBg to OrangeText
        "ชำระแล้ว"    -> GreenBg  to GreenText
        "ตรวจสอบแล้ว" -> Color(0xFFDBEAFE) to Color(0xFF3B82F6)
        else           -> OrangeBg to OrangeText
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(status, color = fg, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

// ─────────────────────────────────────────────
//  FORMAT เงิน
// ─────────────────────────────────────────────
private fun fmtMoney(amount: Double): String =
    NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }.format(amount)