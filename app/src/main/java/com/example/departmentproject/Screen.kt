
package com.example.departmentproject

sealed class Screen(val route: String) {

    object Login : Screen("login")
    object AdminDashboard : Screen("admin_dashboard")
    object TenantHome : Screen("tenant_home")

    object TenantList : Screen("tenant_list")

    object RoomList : Screen("room_list")
    object Payment : Screen(route = "payment")
    object PaymentConfirm : Screen("payment_confirm")

    object EditRoom : Screen("edit_room")
    data object RoomManage: Screen(route = "room_manage_screen")
    data object InsertRoom: Screen(route = "insert_room_screen")
    data object EditDeleteRoom: Screen(route = "edit_delete_room_screen")

    object Finance : Screen("finance")
    object Meter : Screen("meter")
    object Setting : Screen("setting")
    object Edit : Screen("Edit")
    data object Home : Screen(
        route = "home_screen",
    )
    data object Bills : Screen("bills")
    object RegisterOwnerStep1 : Screen("register_owner_step1")
    object RegisterOwnerStep2 : Screen("register_owner_step2")
    object RegisterOwnerStep3 : Screen("register_owner_step3")
    object RegisterOwnerStep4 : Screen("register_owner_step4")

}
