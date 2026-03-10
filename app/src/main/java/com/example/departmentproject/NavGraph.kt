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

    val context = LocalContext.current
    val sharedPrefs = SharedPreferencesManager(context)

    val userId = sharedPrefs.getUserId()
    val ownerId = sharedPrefs.getOwnerId()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        // 🔹 LOGIN
        composable(Screen.Login.route) {
            LoginScreen(navController, vm)
        }

        // 🔹 ADMIN DASHBOARD
        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(vm, navController)
        }

        // 🔹 TENANT LIST
        composable(Screen.TenantList.route) {
            TenantListScreen(vm)
        }

        // 🔹 ROOM LIST
        composable(Screen.RoomList.route) {
            RoomListScreen(vm, navController)
        }

        // 🔹 ROOM MANAGE
        composable(Screen.RoomManage.route) {
            RoomManageScreen(
                navController = navController,
                viewModel = roomViewModel
            )
        }

        // 🔹 EDIT ROOM
        composable(Screen.EditDeleteRoom.route) {
            EditDeleteRoomScreen(
                navController = navController,
                viewModel = roomViewModel
            )
        }

        // 🔹 INSERT ROOM
        composable(Screen.InsertRoom.route) {
            InsertRoomScreen(
                navController = navController,
                viewModel = roomViewModel
            )
        }

        // 🔹 FINANCE
        composable(Screen.Finance.route) {
            BillScreen(navController)
        }

        // 🔹 METER
        composable(Screen.Meter.route) {
            MeterScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // 🔹 SETTINGS
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

        // 🔹 HOME
        composable(Screen.Home.route) {
            HomeScreen(navController, vm)
        }

        // 🔹 BILL LIST
        composable(
            route = "bills/{billId}",
            arguments = listOf(
                navArgument("billId") { type = NavType.IntType }
            )
        ) { backStackEntry ->

            val billId = backStackEntry.arguments?.getInt("billId") ?: 0

            BillDetailScreen(
                navController = navController,
                viewModel = vm,
                billId = billId
            )
        }

        // 🔹 BILL DETAIL
        composable(
            route = "bill_detail/{billId}",
            arguments = listOf(
                navArgument("billId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val billId =
                backStackEntry.arguments?.getInt("billId") ?: 0

            BillDetailScreen(
                navController = navController,
                viewModel = vm,
                billId = billId
            )
        }

        // 🔹 PAYMENT FORM
        composable(
            route = "payment_form/{amount}/{month}/{year}",
            arguments = listOf(
                navArgument("amount") { type = NavType.StringType },
                navArgument("month") { type = NavType.StringType },
                navArgument("year") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val amount =
                backStackEntry.arguments?.getString("amount") ?: "0"

            val month =
                backStackEntry.arguments?.getString("month") ?: ""

            val year =
                backStackEntry.arguments?.getString("year") ?: ""

            PaymentFormScreen(
                amountFromBill = amount,
                month = month,
                year = year,
                userId = userId,
                navController = navController
            )
        }

        // 🔹 REGISTER OWNER
        composable("registerOwner") {
            RegisterOwnerScreen(navController)
        }

        // 🔹 TENANT FORM
        composable(
            route = "tenant_form/{roomId}",
            arguments = listOf(
                navArgument("roomId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val roomId =
                backStackEntry.arguments?.getInt("roomId") ?: 0

            TenantFormScreen(
                navController,
                roomId
            )
        }

        // 🔹 TENANT ACCOUNT
        composable(
            route = "tenant_account/{tenantId}/{roomId}",
            arguments = listOf(
                navArgument("tenantId") {
                    type = NavType.IntType
                },
                navArgument("roomId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val tenantId =
                backStackEntry.arguments?.getInt("tenantId") ?: 0

            val roomId =
                backStackEntry.arguments?.getInt("roomId") ?: 0

            TenantAccountScreen(
                navController,
                tenantId,
                roomId
            )
        }

    }
}