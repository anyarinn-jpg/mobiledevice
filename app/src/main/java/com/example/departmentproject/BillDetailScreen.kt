package com.example.departmentproject

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

private val BDPurple     = Color(0xFF6B6FD4)
private val BDGradEnd    = Color(0xFF8E92E3)
private val BDGreenText  = Color(0xFF0BAB72)
private val BDGreenBg    = Color(0xFFD6F5EB)
private val BDOrangeText = Color(0xFFD97706)
private val BDOrangeBg   = Color(0xFFFEF3C7)
private val BDBgGray     = Color(0xFFF4F5FB)
private val BDWhite      = Color(0xFFFFFFFF)
private val BDTextMain   = Color(0xFF1C1F35)
private val BDTextSub    = Color(0xFF9CA3AF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillDetailScreen(
    navController: NavHostController,
    viewModel: DormitoryViewModel,
    billId: Int
) {
    val context = LocalContext.current

    val bill = viewModel.dashboardData?.recentPayments
        ?.find { it.billId == billId }

    val monthNames = listOf(
        "", "มกราคม", "กุมภาพันธ์", "มีนาคม", "เมษายน",
        "พฤษภาคม", "มิถุนายน", "กรกฎาคม", "สิงหาคม",
        "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"
    )

    Scaffold(
        containerColor = BDBgGray,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        // ✅ title เปลี่ยนตาม status
                        if (bill?.status == "รอตรวจสอบ") "ยืนยันการชำระเงิน" else "รายละเอียดบิล",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = BDTextMain
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = BDTextMain)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BDWhite)
            )
        }
    ) { innerPadding ->

        if (bill == null) {
            Box(
                Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("ไม่พบข้อมูลบิล", color = BDTextSub, fontSize = 15.sp)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── Header Card ────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.horizontalGradient(listOf(BDPurple, BDGradEnd)))
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column {
                    Text(
                        "ห้อง ${bill.roomNumber}",
                        color = BDWhite,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "ประจำเดือน ${monthNames.getOrElse(bill.billMonth) { "${bill.billMonth}" }} ${bill.billYear}",
                        color = Color(0xFFCDD0F0),
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "฿${fmtMoneyBD(bill.amount)}",
                        color = BDWhite,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    BDStatusChip(bill.status)
                }
            }

            // ── Info Card ──────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BDWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "ข้อมูลบิล",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = BDTextMain
                    )
                    Spacer(Modifier.height(12.dp))
                    BDInfoRow("หมายเลขบิล", "#${bill.billId}")
                    BDInfoRow("ห้องพัก", "ห้อง ${bill.roomNumber}")
                    BDInfoRow(
                        "ประจำเดือน",
                        "${monthNames.getOrElse(bill.billMonth) { "${bill.billMonth}" }} ${bill.billYear}"
                    )
                    BDInfoRow("สถานะ", bill.status)
                    Divider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = Color(0xFFEEEEEE)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "ยอดรวม",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = BDTextMain
                        )
                        Text(
                            "฿${fmtMoneyBD(bill.amount)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = BDPurple
                        )
                    }
                }
            }

            // ── ปุ่ม Action ────────────────────────────
            if (bill.status == "รอตรวจสอบ") {

                // ✅ แสดง 2 ปุ่ม: ปฏิเสธ | ยืนยันการชำระเงิน
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // ปุ่ม ปฏิเสธ → กลับหน้าเดิม ไม่เปลี่ยนสถานะ
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, Color(0xFFEEEEEE)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = BDTextMain
                        )
                    ) {
                        Text("ปฏิเสธ", fontSize = 15.sp, color = BDTextMain)
                    }

                    // ✅ ปุ่ม ยืนยันการชำระเงิน → เรียก markInspected → status = "ชำระแล้ว"
                    Button(
                        onClick = {
                            viewModel.markInspected(context, bill.billId) {
                                navController.popBackStack()
                            }
                        },
                        modifier = Modifier
                            .weight(2f)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BDPurple)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle, null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "ยืนยันการชำระเงิน",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

            } else {
                // ✅ สถานะ "ชำระแล้ว" → แสดงแค่ปุ่มกลับ
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BDTextMain)
                ) {
                    Text("กลับ", fontSize = 15.sp, color = BDTextMain)
                }
            }
        }
    }
}

@Composable
private fun BDInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = BDTextSub)
        Text(value, fontSize = 14.sp, color = BDTextMain, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun BDStatusChip(status: String) {
    // ✅ เหลือแค่ 2 สถานะ
    val (bg, fg) = when (status) {
        "รอตรวจสอบ" -> BDOrangeBg to BDOrangeText
        "ชำระแล้ว"  -> BDGreenBg  to BDGreenText
        else         -> BDOrangeBg to BDOrangeText
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(status, color = fg, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

private fun fmtMoneyBD(amount: Double): String =
    java.text.NumberFormat.getNumberInstance(java.util.Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }.format(amount)