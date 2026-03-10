package com.example.departmentproject

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PreviewRooms(vm: RegisterOwnerViewModel) {

    Column {

        Text("Buildings Preview")

        Spacer(Modifier.height(10.dp))

        vm.buildings.forEach { b ->

            Text("Building : ${b.name}")

        }

    }

}