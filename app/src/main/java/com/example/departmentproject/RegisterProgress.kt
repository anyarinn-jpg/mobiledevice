package com.example.departmentproject

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RegisterProgress(step: Int) {

    Column {

        LinearProgressIndicator(
            progress = step / 4f,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Text("Step $step / 4")

        Spacer(Modifier.height(20.dp))
    }
}