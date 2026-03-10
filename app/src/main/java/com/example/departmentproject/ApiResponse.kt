package com.example.departmentproject
import com.google.gson.annotations.SerializedName
data class ApiResponse(
    val error: Boolean,
    val message: String?,
    val room_id: Int?,
    val success: Boolean,
    val tenant_id: Int


)

data class DashboardData(
    @SerializedName("monthly_total") val monthlyTotal: Double,
    @SerializedName("recent_bills")  val recentBills: List<BillSummary>
)

data class BillSummary(
    @SerializedName("bill_id")      val billId: Int,
    @SerializedName("room_number")  val roomNumber: String,
    @SerializedName("total_amount") val totalAmount: Double,
    @SerializedName("status")       val status: String,
    @SerializedName("bill_month")   val billMonth: Int,
    @SerializedName("bill_year")    val billYear: Int

)