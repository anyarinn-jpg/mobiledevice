package com.example.departmentproject

data class PaymentRequest(
    val payment_date: String,
    val payment_month: String,
    val payment_year: String,
    val amount: String,
    val payment_method: String,
    val slip_image: String,
    val user_id: Int
)
