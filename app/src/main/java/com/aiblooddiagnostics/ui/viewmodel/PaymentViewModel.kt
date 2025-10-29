package com.aiblooddiagnostics.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiblooddiagnostics.data.model.Appointment
import com.aiblooddiagnostics.data.repository.BloodDiagnosticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val repository: BloodDiagnosticsRepository
) : ViewModel() {

    fun processPayment(appointmentId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                // Mock payment processing
                kotlinx.coroutines.delay(2000) // Simulate processing time
                
                // Create a mock appointment
                val appointment = Appointment(
                    id = appointmentId,
                    patientId = "current_user", // Mock patient ID
                    doctorId = "doctor_3", // Dr. Amira Mohamed
                    date = "Month 24, Year",
                    time = "10:00 AM",
                    amount = 100.0,
                    currency = "EGP"
                )
                
                repository.insertAppointment(appointment)
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }
}