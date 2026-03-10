package com.example.departmentproject

data class DashboardSummary(
    val adminName: String,
    val totalRooms: Int,
    val occupiedRooms: Int,
    val availableRooms: Int,
    val totalTenants: Int,
    val monthlyIncome: Double
)