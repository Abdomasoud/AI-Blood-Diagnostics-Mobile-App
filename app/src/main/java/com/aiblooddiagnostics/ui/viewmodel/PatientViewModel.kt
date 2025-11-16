package com.aiblooddiagnostics.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiblooddiagnostics.data.api.models.Connection
import com.aiblooddiagnostics.data.api.models.DoctorInfo
import com.aiblooddiagnostics.data.api.models.TestUpload
import com.aiblooddiagnostics.data.manager.SessionManager
import com.aiblooddiagnostics.data.repository.BloodDiagnosticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class PatientViewModel @Inject constructor(
    private val repository: BloodDiagnosticsRepository,
    private val sessionManager: SessionManager,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val _doctors = MutableStateFlow<List<DoctorInfo>>(emptyList())
    val doctors: StateFlow<List<DoctorInfo>> = _doctors.asStateFlow()
    
    private val _testUploads = MutableStateFlow<List<TestUpload>>(emptyList())
    val testUploads: StateFlow<List<TestUpload>> = _testUploads.asStateFlow()
    
    private val _connections = MutableStateFlow<List<Connection>>(emptyList())
    val connections: StateFlow<List<Connection>> = _connections.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _lastUploadId = MutableStateFlow<Int?>(null)
    
    fun loadDoctors() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val doctorsList = repository.getDoctorsFromApi()
                _doctors.value = doctorsList
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadTestUploads() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = sessionManager.getUserId()
                if (userId != null) {
                    val uploads = repository.getPatientUploads(userId)
                    _testUploads.value = uploads
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadConnections() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = sessionManager.getUserId()
                if (userId != null) {
                    val connectionsList = repository.getPatientConnections(userId)
                    _connections.value = connectionsList
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun uploadTestFile(
        fileUri: Uri,
        testType: String,
        notes: String?,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val userId = sessionManager.getUserId()
                if (userId == null) {
                    android.util.Log.e("PatientViewModel", "Upload failed: User not logged in")
                    onResult(false, "User not logged in")
                    return@launch
                }
                
                android.util.Log.d("PatientViewModel", "Starting upload for user: $userId, testType: $testType")
                
                // Copy URI to temporary file
                val file = copyUriToFile(fileUri)
                if (file == null) {
                    android.util.Log.e("PatientViewModel", "Upload failed: Could not copy file from URI")
                    onResult(false, "Failed to read file")
                    return@launch
                }
                
                android.util.Log.d("PatientViewModel", "File copied successfully: ${file.name}, size: ${file.length()} bytes")
                
                val response = repository.uploadTestFile(
                    patientUserId = userId,
                    testType = testType,
                    file = file,
                    notes = notes
                )
                
                // Clean up temp file
                file.delete()
                
                android.util.Log.d("PatientViewModel", "Upload response: $response")
                
                if (response?.success == true) {
                    _lastUploadId.value = response.uploadId
                    loadTestUploads() // Refresh the list
                    android.util.Log.i("PatientViewModel", "Upload successful, uploadId: ${response.uploadId}")
                    onResult(true, "File uploaded successfully")
                } else {
                    android.util.Log.e("PatientViewModel", "Upload failed: ${response?.message}")
                    onResult(false, response?.message ?: "Upload failed")
                }
            } catch (e: Exception) {
                android.util.Log.e("PatientViewModel", "Upload exception: ${e.message}", e)
                e.printStackTrace()
                onResult(false, e.message ?: "Unknown error")
            }
        }
    }
    
    fun requestDoctorConnection(
        doctorUserId: String,
        notes: String?,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val userId = sessionManager.getUserId()
                if (userId == null) {
                    onResult(false, "User not logged in")
                    return@launch
                }
                
                val response = repository.createConnectionRequest(
                    patientUserId = userId,
                    doctorUserId = doctorUserId,
                    testUploadId = _lastUploadId.value,
                    notes = notes
                )
                
                if (response?.success == true) {
                    loadConnections() // Refresh the list
                    onResult(true, "Connection request sent successfully")
                } else {
                    onResult(false, response?.message ?: "Request failed")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false, e.message ?: "Unknown error")
            }
        }
    }
    
    private fun copyUriToFile(uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = File(context.cacheDir, "temp_upload_${System.currentTimeMillis()}")
            
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            inputStream.close()
            
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
