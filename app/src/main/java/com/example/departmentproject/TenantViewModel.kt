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

                val response =
                    RetrofitInstance.api.insertTenant(
                        room_id = roomId,
                        building_id = buildingId
                    )

                if (response.isSuccessful) {

                    val tenantId =
                        response.body()?.tenant_id ?: 0

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