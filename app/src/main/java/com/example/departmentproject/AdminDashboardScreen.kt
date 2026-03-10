package com.example.departmentproject

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

// ─── Color Palette ────────────────────────────────────────────────────────────
private val Primary      = Color(0xFF6B72CA)   // purple
private val BgPage       = Color(0xFFF4F5FB)
private val CardWhite    = Color.White
private val GreenBg      = Color(0xFFDFF5E4)
private val GreenIcon    = Color(0xFF2E7D32)
private val RoomAllBg    = Color(0xFFEAEBFF)
private val RoomFreeBg   = Color(0xFFE4F8EC)
private val GrayText     = Color(0xFF9E9E9E)

@Composable
fun AdminDashboardScreen(
    vm: DormitoryViewModel,
    navController: NavHostController
) {
    val data = vm.dashboardData

    LaunchedEffect(key1 = Unit) {
        vm.getDashboard()
    }

    Scaffold(
        containerColor = BgPage,
        bottomBar      = { AdminBottomBar(navController) }
    ) { padding ->
        LazyColumn(
            modifier            = Modifier
                .padding(padding)
                .background(BgPage)
                .fillMaxSize(),
            contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { TopHeader(data?.summary?.adminName ?: "") }
            item { DashboardCards(data?.summary) }
            item { QuickActionSection(navController) }
            item { RecentActivitySection(data?.recentPayments ?: emptyList()) }
        }
    }
}

// ─── Top Header ───────────────────────────────────────────────────────────────
@Composable
fun TopHeader(name: String) {
    Card(
        shape  = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar circle
                Box(
                    modifier        = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD0D3F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint   = Primary,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("ยินดีต้อนรับ", fontSize = 12.sp, color = GrayText)
                    Text(
                        "$name",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 15.sp,
                        color      = Color(0xFF1A1A2E)
                    )
                }
            }

            // Notification bell with badge
            Box {
                IconButton(onClick = {}) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color(0xFF555580)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFFE53935), CircleShape)
                        .align(Alignment.TopEnd)
                        .offset(x = (-6).dp, y = 6.dp)
                )
            }
        }
    }
}

// ─── Dashboard Cards ──────────────────────────────────────────────────────────
@Composable
fun DashboardCards(summary: DashboardSummary?) {
    summary ?: return

    // Row: Total rooms | Free rooms
    Row(
        modifier            = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DashboardCard(
            title   = "ห้องทั้งหมด",
            value   = "${summary.totalRooms} ห้อง",
            iconRes = Icons.Default.Apartment,
            bgColor = RoomAllBg,
            modifier = Modifier.weight(1f)
        )
        DashboardCard(
            title   = "ห้องว่าง",
            value   = "${summary.availableRooms} ห้อง",
            iconRes = Icons.Default.MeetingRoom,
            bgColor = RoomFreeBg,
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(Modifier.height(4.dp))

    // Monthly income card
    Card(
        shape  = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment   = Alignment.CenterVertically
        ) {
            Column {
                Text("รายได้ประจำเดือนนี้", fontSize = 13.sp, color = GrayText)
                Text(
                    "฿${summary.monthlyIncome}",
                    fontSize   = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color(0xFF1A1A2E)
                )
            }
            Box(
                modifier        = Modifier
                    .size(46.dp)
                    .background(Color(0xFFEAEBFF), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun DashboardCard(
    title   : String,
    value   : String,
    iconRes : ImageVector,
    bgColor : Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape    = RoundedCornerShape(20.dp),
        colors   = CardDefaults.cardColors(containerColor = CardWhite),
        modifier = modifier
    ) {
        Column(Modifier.padding(16.dp)) {
            Box(
                modifier        = Modifier
                    .size(38.dp)
                    .background(bgColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(iconRes, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(title, fontSize = 12.sp, color = GrayText)
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
        }
    }
}

// ─── Quick Actions ────────────────────────────────────────────────────────────


@Composable
fun QuickActionSection(navController: NavHostController) {

    var selectedIndex by remember { mutableStateOf(-1) }

    val actions = listOf(
        QuickAction("ผู้เช่า", Icons.Default.PersonAdd, Screen.TenantList.route),
        QuickAction(
            "ห้องพัก",
            Icons.Default.Apartment,
            Screen.RoomManage.route
        ),
        QuickAction("ออกบิล", Icons.Default.Unsubscribe, Screen.Meter.route)
    )

    Column {
        Text(
            "ดำเนินการด่วน",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color(0xFF1A1A2E)
        )

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {

            actions.forEachIndexed { index, action ->

                val isSelected = selectedIndex == index

                ActionButton(
                    text = action.label,
                    icon = action.icon,
                    isSelected = isSelected,
                    onClick = {
                        selectedIndex = index
                        navController.navigate(action.route)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val bgColor = if (isSelected) Primary else Color.White
    val contentColor = if (isSelected) Color.White else Primary

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = text,
                tint = contentColor,
                modifier = Modifier.size(26.dp)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text,
                color = contentColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ─── Recent Activity ──────────────────────────────────────────────────────────
@Composable
fun RecentActivitySection(list: List<RecentPayment>) {
    Column {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text("ความเคลื่อนไหวล่าสุด", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1A1A2E))
            TextButton(onClick = {}) {
                Text("ดูทั้งหมด", color = Primary, fontSize = 13.sp)
            }
        }

        Spacer(Modifier.height(4.dp))

        list.forEach { payment ->
            Card(
                shape    = RoundedCornerShape(16.dp),
                colors   = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier        = Modifier
                                .size(36.dp)
                                .background(GreenBg, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, null, tint = GreenIcon, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "ชำระค่าเช่าแล้ว - ห้อง ${payment.roomNumber}",
                                fontWeight = FontWeight.Medium,
                                fontSize   = 14.sp,
                                color      = Color(0xFF1A1A2E)
                            )
                            Text(payment.timeText, fontSize = 12.sp, color = GrayText)
                        }
                    }
                    Text(
                        "฿${payment.amount}",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 14.sp,
                        color      = Color(0xFF1A1A2E)
                    )
                }
            }
        }
    }
}

// ─── Bottom Navigation ────────────────────────────────────────────────────────
@Composable
fun AdminBottomBar(navController: NavHostController) {

    val currentRoute =
        navController.currentBackStackEntry?.destination?.route

    NavigationBar(containerColor = CardWhite) {

        NavigationBarItem(
            selected = currentRoute == Screen.AdminDashboard.route,
            onClick = {
                navController.navigate(Screen.AdminDashboard.route) {
                    popUpTo(Screen.AdminDashboard.route)
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.Dashboard, null) },
            label = { Text("หน้าแรก") }
        )

        NavigationBarItem(
            selected = currentRoute == Screen.RoomManage.route,
            onClick = {
                navController.navigate(Screen.RoomManage.route) {
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.Apartment, null) },
            label = { Text("ห้องพัก") }
        )

        NavigationBarItem(
            selected = currentRoute == Screen.Home.route,
            onClick = {
                navController.navigate(Screen.Home.route) {
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.AccountBalanceWallet, null) },
            label = { Text("การเงิน") }
        )

        NavigationBarItem(
                selected = currentRoute == Screen.Setting.route,
            onClick = {
                navController.navigate(Screen.Setting.route) {
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.Settings, null) },
            label = { Text("ตั้งค่า") }
        )
    }
}