package com.example.departmentproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TenantViewModel : ViewModel() {

    fun insertTenant(
        roomId: Int,
        buildingId: Int,
        onSuccess: (Int) -> Unit,
        onError: (String) -> Unit
    ) {

        viewModelScope.launch {

            try {

                val request = TenantRequest(
                    first_name = "",
                    last_name = "",
                    phone = "",
                    email = "",
                    check_in_date = "",
                    check_out_date = "",
                    room_id = roomId
                )

                val response = RetrofitInstance.api.insertTenant(request)

                if (response.isSuccessful) {

                    val tenantId = response.body()?.tenant_id ?: 0
                    onSuccess(tenantId)

                } else {

                    onError("Insert tenant failed")

                }

            } catch (e: Exception) {

                onError(e.message ?: "Error")

            }

        }
    }
}