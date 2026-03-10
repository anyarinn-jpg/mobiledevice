package com.example.departmentproject

data class UtilityBill(
    val bill_id: Int,
    val room_id: Int,
    val bill_month: Int,
    val bill_year: Int,
    val water_unit: Int,
    val electric_unit: Int,
    val total_amount: Double,
    val status: String?
)
