package com.example.departmentproject

data class DashboardResponse(
    val summary: DashboardSummary,
    val recentPayments: List<RecentPayment>
)