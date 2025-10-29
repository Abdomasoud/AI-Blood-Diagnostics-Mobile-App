package com.aiblooddiagnostics.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiblooddiagnostics.data.model.*
import com.aiblooddiagnostics.data.repository.BloodDiagnosticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class SaveState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class DiagnosisViewModel @Inject constructor(
    private val repository: BloodDiagnosticsRepository
) : ViewModel() {

    val doctors = repository.getAllDoctors()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _saveState = MutableStateFlow(SaveState())
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    fun saveDiagnosis(
        doctorId: String,
        patientName: String,
        patientAge: Int,
        gender: String,
        testType: String,
        fileType: String,
        fileUri: String?,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            _saveState.value = _saveState.value.copy(isLoading = true, error = null)
            
            try {
                // Create patient
                val patient = Patient(
                    id = UUID.randomUUID().toString(),
                    fullName = patientName,
                    age = patientAge,
                    gender = gender
                )
                
                repository.insertPatient(patient)
                
                // Create diagnosis
                val diagnosis = Diagnosis(
                    id = UUID.randomUUID().toString(),
                    patientId = patient.id,
                    doctorId = doctorId,
                    testType = testType,
                    fileType = fileType,
                    filePath = fileUri,
                    status = "pending"
                )
                
                repository.insertDiagnosis(diagnosis)
                
                _saveState.value = _saveState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    error = null
                )
                onResult(true)
                
            } catch (e: Exception) {
                _saveState.value = _saveState.value.copy(
                    isLoading = false,
                    error = "Failed to save diagnosis: ${e.message}"
                )
                onResult(false)
            }
        }
    }
}