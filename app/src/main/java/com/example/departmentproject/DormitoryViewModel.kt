package com.example.departmentproject

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DormitoryViewModel : ViewModel() {

    // ─────────────────────────────
    // Login
    // ─────────────────────────────
    private var _loginResult by mutableStateOf<LoginClass?>(null)
    val loginResult: LoginClass?
        get() = _loginResult

    fun resetLoginResult() {
        _loginResult = null
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val response = DormitoryClient.api.login(
                    mapOf(
                        "username" to username,
                        "password" to password
                    )
                )

                if (response.isSuccessful && response.body() != null) {
                    _loginResult = response.body()
                } else {
                    _loginResult = LoginClass(
                        error = true,
                        message = "Login Failed",
                        user_id = null,
                        role = null,
                        owner_id = null
                    )
                }
            } catch (e: Exception) {
                _loginResult = LoginClass(
                    error = true,
                    message = e.message ?: "Unknown error",
                    user_id = null,
                    role = null,
                    owner_id = null
                )
            }
        }
    }

    // ─────────────────────────────
    // Dashboard
    // ─────────────────────────────
    var dashboardData by mutableStateOf<DashboardResponse?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun getDashboard() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response = DormitoryClient.api.getDashboard()

                Log.d("API_DEBUG", "Code: ${response.code()}")
                Log.d("API_DEBUG", "Body: ${response.body()}")

                if (response.isSuccessful && response.body() != null) {
                    dashboardData = response.body()
                } else {
                    errorMessage =
                        "Server error: ${response.code()} - ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                errorMessage = "เชื่อมต่อไม่ได้: ${e.message}"
                Log.e("DormitoryViewModel", "Exception: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    // ─────────────────────────────
    // Update User
    // ─────────────────────────────
    fun updateUser(id: Int, username: String, password: String) {
        viewModelScope.launch {
            try {
                val data = mutableMapOf<String, String>()
                data["username"] = username

                if (password.isNotEmpty()) {
                    data["password"] = password
                }

                DormitoryClient.api.updateUser(id, data)

            } catch (e: Exception) {
                Log.e("DormitoryViewModel", "Update user error: ${e.message}")
            }
        }
    }

    // ─────────────────────────────
    // Confirm Payment
    // ─────────────────────────────
    fun markInspected(
        context: Context,
        billId: Int,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = DormitoryClient.api.updateBillStatus(
                    mapOf(
                        "bill_id" to billId.toString(),
                        "status" to "ชำระแล้ว"
                    )
                )

                if (response.isSuccessful) {
                    Toast.makeText(
                        context,
                        "ยืนยันการชำระเงินสำเร็จ",
                        Toast.LENGTH_SHORT
                    ).show()

                    onSuccess()
                    getDashboard() // refresh dashboard

                } else {
                    Toast.makeText(
                        context,
                        "อัปเดตไม่สำเร็จ",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()

                Log.e("DormitoryViewModel", "Update bill error: ${e.message}")
            }
        }
    }
}