package com.example.departmentproject

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun RegisterOwnerScreen(navController: NavHostController) {

    val vm: RegisterOwnerViewModel = viewModel()

    var step by remember { mutableStateOf(1) }

    when (step) {

        1 -> RegisterOwnerStep1(vm) { step = 2 }

        2 -> RegisterOwnerStep2(vm) { step = 3 }

        3 -> RegisterOwnerStep3(vm) { step = 4 }

        4 -> RegisterOwnerStep4(vm, navController)

    }

}