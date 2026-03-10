package com.example.departmentproject

data class RecentPayment(
    val billId: Int,
    val roomNumber: String,
    val billMonth: Int,
    val billYear: Int,
    val amount: Double,
    val status: String,
    val timeText: String
)