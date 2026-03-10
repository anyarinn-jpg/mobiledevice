package com.example.departmentproject

data class RecentPayment(
    val billId: Int,
    val roomId: Int,
    val roomNumber: String,
    val waterUnit: Int,
    val electricUnit: Int,
    val amount: Double,
    val roomPrice: Double,
    val status: String,
    val billMonth: Int,
    val billYear: Int,
    val timeText: String,
    val userId: Int?,
    val slipImage: String?
)