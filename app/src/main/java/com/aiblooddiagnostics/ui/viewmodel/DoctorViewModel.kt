package com.aiblooddiagnostics.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiblooddiagnostics.data.api.models.Connection
import com.aiblooddiagnostics.data.repository.BloodDiagnosticsRepository
import com.aiblooddiagnostics.data.manager.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DoctorViewModel @Inject constructor(
    private val repository: BloodDiagnosticsRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _pendingConnections = MutableStateFlow<List<Connection>>(emptyList())
    val pendingConnections: StateFlow<List<Connection>> = _pendingConnections.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadPendingConnections() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val userId = sessionManager.getUserId()
                if (userId != null) {
                    android.util.Log.d("DoctorViewModel", "Loading connections for doctor: $userId")
                    val connections = repository.getDoctorConnections(userId)
                    // Filter only pending connections
                    _pendingConnections.value = connections?.filter { it.status == "pending" } ?: emptyList()
                    android.util.Log.d("DoctorViewModel", "Loaded ${_pendingConnections.value.size} pending connections")
                }
            } catch (e: Exception) {
                android.util.Log.e("DoctorViewModel", "Error loading connections", e)
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun approveConnection(
        connectionId: Int,
        approve: Boolean,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                android.util.Log.d("DoctorViewModel", "Approving connection $connectionId: $approve")
                val response = repository.approveConnection(connectionId, approve)
                
                if (response?.success == true) {
                    val message = if (approve) "Connection approved! Chat room created." else "Connection rejected."
                    android.util.Log.i("DoctorViewModel", message)
                    onResult(true, message)
                } else {
                    android.util.Log.e("DoctorViewModel", "Approval failed: ${response?.message}")
                    onResult(false, response?.message ?: "Failed to process request")
                }
            } catch (e: Exception) {
                android.util.Log.e("DoctorViewModel", "Error approving connection", e)
                e.printStackTrace()
                onResult(false, e.message ?: "Unknown error")
            }
        }
    }
}
