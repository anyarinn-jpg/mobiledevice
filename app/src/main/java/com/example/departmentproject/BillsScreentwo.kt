package com.example.departmentproject

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

private val Purple      = Color(0xFF6B6FD4)
private val BgGray      = Color(0xFFF4F5FB)
private val White       = Color(0xFFFFFFFF)
private val TextMain    = Color(0xFF1C1F35)
private val TextSub     = Color(0xFF9CA3AF)
private val GreenText   = Color(0xFF0BAB72)
private val GreenBg     = Color(0xFFD6F5EB)
private val OrangeText  = Color(0xFFD97706)
private val OrangeBg    = Color(0xFFFEF3C7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillsScreentwo(
    navController: NavHostController,
    viewModel: DormitoryViewModel
) {
    val context = LocalContext.current
    var search by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ทั้งหมด") }

    // ✅ เอา "ตรวจสอบแล้ว" ออก เหลือแค่ 2 สถานะ
    val filters = listOf("ทั้งหมด", "รอตรวจสอบ", "ชำระแล้ว")

    val bills = viewModel.dashboardData?.recentPayments
        ?.filter { bill ->
            val matchSearch = search.isBlank() ||
                    bill.roomNumber.contains(search, ignoreCase = true)
            val matchFilter = selectedFilter == "ทั้งหมด" ||
                    bill.status == selectedFilter
            matchSearch && matchFilter
        } ?: emptyList()

    Scaffold(
        containerColor = BgGray,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "บิลทั้งหมด",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextMain
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextMain)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp,
                top = 16.dp, bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── Search Bar ────────────────────────────
            item {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    placeholder = {
                        Text("ค้นหาห้องพัก...", color = TextSub, fontSize = 14.sp)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, null, tint = TextSub)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Purple,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = White,
                        unfocusedContainerColor = White
                    )
                )
            }

            // ── Filter Chips ──────────────────────────
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filters) { filter ->
                        val isSelected = filter == selectedFilter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) Purple else White)
                                .clickable { selectedFilter = filter }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = filter,
                                color = if (isSelected) White else TextSub,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }

            // ── Summary ───────────────────────────────
            item {
                Text(
                    "พบ ${bills.size} รายการ",
                    fontSize = 13.sp,
                    color = TextSub
                )
            }

            // ── Loading ───────────────────────────────
            if (viewModel.isLoading) {
                item {
                    Box(
                        Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator(color = Purple) }
                }
            }

            // ── Bill List ─────────────────────────────
            items(bills) { bill ->
                BillsCard(
                    bill = bill,
                    onInspect = {
                        navController.navigate("bill_detail/${bill.billId}")
                    },
                    onDetail = {
                        navController.navigate("bill_detail/${bill.billId}")
                    }
                )
            }

            // ── Empty State ───────────────────────────
            if (!viewModel.isLoading && bills.isEmpty()) {
                item {
                    Box(
                        Modifier.fillMaxWidth().padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "ไม่พบรายการบิล",
                            color = TextSub,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BillsCard(
    bill: RecentPayment,
    onInspect: () -> Unit,
    onDetail: () -> Unit
) {
    val monthNames = listOf(
        "", "ม.ค.", "ก.พ.", "มี.ค.", "เม.ย.", "พ.ค.", "มิ.ย.",
        "ก.ค.", "ส.ค.", "ก.ย.", "ต.ค.", "พ.ย.", "ธ.ค."
    )
    val monthLabel = monthNames.getOrElse(bill.billMonth) { "${bill.billMonth}" }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BillsStatusChip(bill.status)
                Text(
                    "$monthLabel ${bill.billYear}",
                    fontSize = 12.sp,
                    color = TextSub
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "ห้อง ${bill.roomNumber}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextMain
            )

            Text(
                "฿${fmtMoney2(bill.amount)}",
                color = Purple,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
            )

            if (bill.status == "รอตรวจสอบ") {
                Button(
                    onClick = onInspect,
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple)
                ) {
                    Text("ตรวจสอบ", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
            } else {
                OutlinedButton(
                    onClick = onDetail,
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMain)
                ) {
                    Text("ดูรายละเอียด", fontSize = 14.sp, color = TextMain)
                }
            }
        }
    }
}

@Composable
private fun BillsStatusChip(status: String) {
    // ✅ เอา "ตรวจสอบแล้ว" ออก เหลือแค่ 2 สถานะ
    val (bg, fg) = when (status) {
        "รอตรวจสอบ" -> OrangeBg to OrangeText
        "ชำระแล้ว"  -> GreenBg  to GreenText
        else         -> OrangeBg to OrangeText
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

private fun fmtMoney2(amount: Double): String =
    java.text.NumberFormat.getNumberInstance(java.util.Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }.format(amount)