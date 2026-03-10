package com.example.departmentproject

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsertRoomScreen(
    navController: NavHostController,
    viewModel: RoomViewModel
){
    val context = LocalContext.current
    val sharedPrefs = SharedPreferencesManager(context)
    val ownerId = sharedPrefs.getOwnerId()
    
    val statusOptions = listOf("ว่าง", "ไม่ว่าง")

    // Dropdown States
    var buildingExpanded by remember { mutableStateOf(false) }
    var roomTypeExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(ownerId) {

        viewModel.ownerId = ownerId   // ⭐ ใส่บรรทัดนี้

        if (ownerId != 0) {
            viewModel.fetchBuildingsAndRoomTypes(ownerId)
        }

        viewModel.clearForm()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("เพิ่มข้อมูลห้องพัก", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Owner ID: ${viewModel.ownerId}", fontSize = 12.sp, color = Color.Gray)

            OutlinedTextField(
                value = viewModel.roomNumberText,
                onValueChange = { viewModel.roomNumberText = it },
                label = { Text("เลขห้อง") },
                modifier = Modifier.fillMaxWidth()
            )

            // Radio buttons for Status
            Column(Modifier.fillMaxWidth()) {
                Text(
                    text = "สถานะ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    statusOptions.forEach { status ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.statusText = status }
                        ) {
                            RadioButton(
                                selected = (viewModel.statusText == status),
                                onClick = { viewModel.statusText = status }
                            )
                            Text(
                                text = status,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }

            // Building Dropdown
            ExposedDropdownMenuBox(
                expanded = buildingExpanded,
                onExpandedChange = { buildingExpanded = !buildingExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                val selectedBuilding = viewModel.buildings.find { it.building_id == viewModel.selectedBuildingId }
                OutlinedTextField(
                    value = selectedBuilding?.building_name ?: "เลือกอาคาร",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("อาคาร") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = buildingExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = buildingExpanded,
                    onDismissRequest = { buildingExpanded = false }
                ) {
                    viewModel.buildings.forEach { building ->
                        DropdownMenuItem(
                            text = { Text(building.building_name) },
                            onClick = {
                                viewModel.selectedBuildingId = building.building_id
                                buildingExpanded = false
                            }
                        )
                    }
                }
            }

            // Room Type Dropdown
            ExposedDropdownMenuBox(
                expanded = roomTypeExpanded,
                onExpandedChange = { roomTypeExpanded = !roomTypeExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                val selectedType = viewModel.roomTypes.find { it.room_type_id == viewModel.selectedRoomTypeId }
                OutlinedTextField(
                    value = selectedType?.type_name ?: "เลือกประเภทห้อง",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("ประเภทห้อง") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roomTypeExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = roomTypeExpanded,
                    onDismissRequest = { roomTypeExpanded = false }
                ) {
                    viewModel.roomTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text("${type.type_name} (${type.price} บาท)") },
                            onClick = {
                                viewModel.selectedRoomTypeId = type.room_type_id
                                roomTypeExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = viewModel.pictureUrlText,
                onValueChange = { viewModel.pictureUrlText = it },
                label = { Text("URL รูปภาพ (ถ้ามี)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (viewModel.roomNumberText.isBlank() || viewModel.selectedBuildingId == 0 || viewModel.selectedRoomTypeId == 0) {
                        Toast.makeText(context, "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val room = Room(
                        room_id = 0,
                        room_number = viewModel.roomNumberText,
                        status = viewModel.statusText,
                        building_id = viewModel.selectedBuildingId,
                        room_type_id = viewModel.selectedRoomTypeId,
                        picture = viewModel.pictureUrlText.ifEmpty { null }
                    )

                    viewModel.insertRoom(room, context, viewModel.ownerId) {
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E8CE0))
            ) {
                Text("เพิ่มห้องพัก", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}