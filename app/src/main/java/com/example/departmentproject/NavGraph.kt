package com.example.departmentproject

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun NavGraph(navController: NavHostController) {

    val vm: DormitoryViewModel = viewModel()
    val roomViewModel: RoomViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
    ) {

        composable(Screen.Login.route) {
            LoginScreen(navController, vm)
        }

        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(vm, navController)
        }

        composable(Screen.TenantHome.route) {
            TenantHomeScreen(vm, navController)
        }

        composable(Screen.TenantList.route) {
            TenantListScreen(vm)
        }

        composable(Screen.RoomList.route) {
            RoomListScreen(vm, navController)
        }
        composable(route = Screen.RoomManage.route) {
            RoomManageScreen(navController = navController, viewModel = roomViewModel)
        }

        composable(route = Screen.EditDeleteRoom.route) {
            EditDeleteRoomScreen(navController = navController, viewModel = roomViewModel)
        }

        composable(route = Screen.InsertRoom.route) {
            InsertRoomScreen(navController = navController, viewModel = roomViewModel)
        }
        composable(Screen.Finance.route) {
            BillScreen(navController)
        }
        composable(Screen.Meter.route) {
            MeterScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.PaymentConfirm.route) {
            PaymentConfirmScreen(
                onConfirm = {
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Setting.route) {
            SettingScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(navController, vm)
        }

        composable("bills") {
            BillsScreentwo(navController = navController, viewModel = vm)
        }
        composable(
            route = "bill_detail/{billId}",
            arguments = listOf(navArgument("billId") { type = NavType.IntType })
        ) { backStackEntry ->
            val billId = backStackEntry.arguments?.getInt("billId") ?: 0
            BillDetailScreen(
                navController = navController,
                viewModel = vm,
                billId = billId
            )
        }
        composable(
            route = "payment_form/{amount}/{month}/{year}",
            arguments = listOf(
                navArgument("amount") { type = NavType.StringType },
                navArgument("month") { type = NavType.StringType },
                navArgument("year") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val amount = backStackEntry.arguments?.getString("amount") ?: "0"
            val month = backStackEntry.arguments?.getString("month") ?: ""
            val year = backStackEntry.arguments?.getString("year") ?: ""

            PaymentFormScreen(
                amountFromBill = amount,
                month = month,
                year = year,
                onNext = { navController.navigate(Screen.PaymentConfirm.route) },
                onCancel = { navController.popBackStack() }
            )
        }
        composable("registerOwner") {
            RegisterOwnerScreen(navController)
        }

//tenant
        composable(
            "tenant_form/{roomId}",
            arguments = listOf(
                navArgument("roomId") { type = NavType.IntType }
            )
        ) { backStackEntry ->

            val roomId = backStackEntry.arguments?.getInt("roomId") ?: 0

            TenantFormScreen(navController, roomId)

        }

        composable(
            "tenant_account/{roomId}",
            arguments = listOf(
                navArgument("roomId") { type = NavType.IntType }
            )
        ) { backStackEntry ->

            val roomId = backStackEntry.arguments?.getInt("roomId") ?: 0

            val context = LocalContext.current
            val ownerId = SharedPreferencesManager(context).getOwnerId()

            TenantAccountScreen(navController, roomId, ownerId)

        }
        composable(
            "tenant_form/{roomId}"
        ) {

            val roomId =
                it.arguments?.getString("roomId")!!.toInt()

            TenantFormScreen(
                navController,
                roomId
            )

        }

        composable(
            "tenant_account/{tenantId}/{roomId}"
        ) {

            val tenantId =
                it.arguments?.getString("tenantId")!!.toInt()

            val roomId =
                it.arguments?.getString("roomId")!!.toInt()

            TenantAccountScreen(
                navController,
                tenantId,
                roomId
            )

        }

    }

}