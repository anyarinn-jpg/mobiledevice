package com.example.departmentproject

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomManageScreen(navController: NavHostController, viewModel: RoomViewModel = viewModel()) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val sharedPrefs = SharedPreferencesManager(context)
    val ownerId = sharedPrefs.getOwnerId()
    println("OWNER ID FROM PREF = $ownerId")

    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedRoomId by remember { mutableIntStateOf(-1) }
    
    var selectedFilter by remember { mutableStateOf("ทั้งหมด") }
    val currentFilter by rememberUpdatedState(selectedFilter)

    // Start loading data when the screen is first composed
    LaunchedEffect(Unit) {
        if (ownerId != 0) {
            viewModel.ownerId = ownerId
            viewModel.getAllRooms(ownerId)
        }else {
            Toast.makeText(context, "ไม่พบ User ID, กรุณาล็อกอินใหม่", Toast.LENGTH_LONG).show()
        }
    }

    // Refresh data when returning to the screen
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && viewModel.ownerId != 0) {
                when (currentFilter) {
                    "ทั้งหมด" -> viewModel.getAllRooms(viewModel.ownerId)
                    "ว่าง" -> viewModel.getAvailableRooms(viewModel.ownerId)
                    "ไม่ว่าง" -> viewModel.getUnavailableRooms(viewModel.ownerId)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("การจัดการห้องพัก", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(
                        onClick = { navController.navigate(Screen.InsertRoom.route) },
                        modifier = Modifier
                            .padding(8.dp)
                            .background(Color(0xFF7E8CE0), CircleShape)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Room", tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            AdminBottomBar(navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            Text("Owner ID: ${viewModel.ownerId}", modifier = Modifier.padding(start = 16.dp), fontSize = 12.sp, color = Color.Gray)

            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = { viewModel.searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("ค้นหาเลขห้อง...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryCard("ทั้งหมด", "${viewModel.totalCount}", Modifier.weight(1f))
                SummaryCard("ไม่ว่าง", "${viewModel.unavailableCount}", Modifier.weight(1f))
                SummaryCard("ว่าง", "${viewModel.availableCount}", Modifier.weight(1f))
            }

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == "ทั้งหมด",
                    onClick = {
                        selectedFilter = "ทั้งหมด"
                        viewModel.getAllRooms(viewModel.ownerId)
                    },
                    label = { Text("ทั้งหมด") }
                )
                FilterChip(
                    selected = selectedFilter == "ว่าง",
                    onClick = {
                        selectedFilter = "ว่าง"
                        viewModel.getAvailableRooms(viewModel.ownerId)
                    },
                    label = { Text("ว่าง") }
                )
                FilterChip(
                    selected = selectedFilter == "ไม่ว่าง",
                    onClick = {
                        selectedFilter = "ไม่ว่าง"
                        viewModel.getUnavailableRooms(viewModel.ownerId)
                    },
                    label = { Text("ไม่ว่าง") }
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(viewModel.filteredRoomList) { room ->
                    RoomItem(
                        room = room,
                        onEditClick = {
                            viewModel.setRoomForEdit(room)
                            navController.navigate(Screen.EditDeleteRoom.route)
                        },
                        onDeleteClick = {
                            selectedRoomId = room.room_id
                            showDeleteDialog = true
                        },
                        onToggleStatus = {
                            viewModel.toggleRoomStatus(context, room, viewModel.ownerId)
                        },
                        onManageTenant = {
                            navController.navigate("tenant_form/${room.room_id}")
                        }
                    )

                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("ยืนยันการลบ") },
            text = { Text("คุณต้องการลบห้องนี้จริงหรือไม่?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteRoom(context, selectedRoomId, viewModel.ownerId) {
                        showDeleteDialog = false
                    }
                }) {
                    Text("ตกลง", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("ยกเลิก")
                }
            }
        )
    }
}

@Composable
fun RoomItem(
    room: Room,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleStatus: () -> Unit,
    onManageTenant: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Apartment, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(text = "ห้อง ${room.room_number}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(text = "ประเภท ID: ${room.room_type_id}", color = Color.Gray, fontSize = 14.sp)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        val isAvailable = room.status == "ว่าง"
                        Text(
                            text = if (isAvailable) "สถานะว่าง" else "สถานะไม่ว่าง",
                            color = if (isAvailable) Color(0xFF4CAF50) else Color(0xFF7E8CE0),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Switch(
                            checked = !isAvailable,
                            onCheckedChange = { onToggleStatus() },
                            modifier = Modifier.scale(0.7f),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF7E8CE0)
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier.padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onEditClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5)),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.DarkGray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("แก้ไข", color = Color.DarkGray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onManageTenant()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E8CE0)),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("จัดการผู้เช่า", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier.size(40.dp)
                            .background(Color(0xFFFFEBEE), shape = RoundedCornerShape(8.dp))
                            .clickable { onDeleteClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryCard(label: String, count: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(count, fontWeight = FontWeight.Bold, fontSize = 24.sp)
        }
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = if (isSelected) Color(0xFF7E8CE0) else Color.Gray
        )
        Text(
            label,
            color = if (isSelected) Color(0xFF7E8CE0) else Color.Gray,
            fontSize = 10.sp
        )
    }
}