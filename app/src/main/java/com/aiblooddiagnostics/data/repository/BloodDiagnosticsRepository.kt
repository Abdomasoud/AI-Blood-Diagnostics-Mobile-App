package com.aiblooddiagnostics.data.repository

import android.net.Uri
import com.aiblooddiagnostics.data.api.BloodDiagnosticsApi
import com.aiblooddiagnostics.data.api.models.*
import com.aiblooddiagnostics.data.dao.*
import com.aiblooddiagnostics.data.model.*
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BloodDiagnosticsRepository @Inject constructor(
    private val api: BloodDiagnosticsApi,
    private val userDao: UserDao,
    private val doctorDao: DoctorDao,
    private val patientDao: PatientDao,
    private val diagnosisDao: DiagnosisDao,
    private val appointmentDao: AppointmentDao,
    private val chatDao: ChatDao
) {
    // User operations - API based
    suspend fun loginUser(email: String, password: String): User? {
        return try {
            // Determine userType based on email (doctors typically use @hospital.com)
            val userType = if (email.contains("@hospital.com") || email.contains("doctor")) "doctor" else "patient"
            val response = api.login(LoginRequest(email, password, userType))
            if (response.isSuccessful && response.body()?.success == true) {
                val authResponse = response.body()!!
                User(
                    id = authResponse.userId ?: "",
                    fullName = authResponse.fullName ?: "",
                    email = authResponse.email ?: "",
                    password = "",
                    userType = authResponse.userType ?: "patient",
                    mobileNumber = null,
                    dateOfBirth = null
                )
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun registerUser(user: User): Boolean {
        return try {
            val request = SignupRequest(
                fullName = user.fullName,
                email = user.email,
                password = user.password,
                userType = user.userType,
                mobileNumber = user.mobileNumber,
                dateOfBirth = user.dateOfBirth,
                gender = user.gender,
                bloodType = user.bloodType,
                specialization = user.specialization,
                experienceYears = user.experienceYears
            )
            val response = api.signup(request)
            response.isSuccessful && response.body()?.success == true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getUserByEmail(email: String): User? = null // Not used with API

    // Doctor operations
    fun getAllDoctors(): Flow<List<Doctor>> = doctorDao.getAllDoctors()

    suspend fun getDoctorById(id: String): Doctor? = doctorDao.getDoctorById(id)

    suspend fun insertDoctors(doctors: List<Doctor>) = doctorDao.insertDoctors(doctors)

    // Patient operations
    fun getAllPatients(): Flow<List<Patient>> = patientDao.getAllPatients()

    suspend fun insertPatient(patient: Patient) = patientDao.insertPatient(patient)

    suspend fun deletePatient(patient: Patient) = patientDao.deletePatient(patient)

    suspend fun deletePatientById(patientId: String) = patientDao.deletePatientById(patientId)

    // Get patients who chose a specific doctor
    fun getPatientsByDoctor(doctorId: String): Flow<List<Patient>> = 
        patientDao.getPatientsByDoctor(doctorId)

    // Diagnosis operations
    fun getAllDiagnoses(): Flow<List<Diagnosis>> = diagnosisDao.getAllDiagnoses()

    suspend fun insertDiagnosis(diagnosis: Diagnosis) = diagnosisDao.insertDiagnosis(diagnosis)

    suspend fun updateDiagnosis(diagnosis: Diagnosis) = diagnosisDao.updateDiagnosis(diagnosis)

    fun getDiagnosesByStatus(status: String): Flow<List<Diagnosis>> = 
        diagnosisDao.getDiagnosesByStatus(status)

    suspend fun deleteDiagnosesByPatient(patientId: String) = 
        diagnosisDao.deleteDiagnosesByPatient(patientId)

    // Get diagnoses for a specific doctor
    fun getDiagnosesByDoctor(doctorId: String): Flow<List<Diagnosis>> = 
        diagnosisDao.getDiagnosesByDoctor(doctorId)

    // Appointment operations
    fun getAllAppointments(): Flow<List<Appointment>> = appointmentDao.getAllAppointments()

    suspend fun insertAppointment(appointment: Appointment) = appointmentDao.insertAppointment(appointment)

    suspend fun getAppointmentById(id: String): Appointment? = appointmentDao.getAppointmentById(id)

    // Chat operations
    fun getChatRoomsForDoctor(doctorId: String): Flow<List<ChatRoom>> = 
        chatDao.getChatRoomsForDoctor(doctorId)

    fun getChatRoomsForPatient(patientId: String): Flow<List<ChatRoom>> = 
        chatDao.getChatRoomsForPatient(patientId)

    suspend fun getChatRoom(roomId: String): ChatRoom? = chatDao.getChatRoom(roomId)

    suspend fun createChatRoom(chatRoom: ChatRoom) = chatDao.insertChatRoom(chatRoom)

    suspend fun updateChatRoom(chatRoom: ChatRoom) = chatDao.updateChatRoom(chatRoom)

    fun getMessagesForRoom(roomId: String): Flow<List<ChatMessage>> = 
        chatDao.getMessagesForRoom(roomId)

    suspend fun sendMessage(message: ChatMessage) {
        chatDao.insertMessage(message)
        // Update chat room with last message
        val roomId = message.chatRoomId
        val room = chatDao.getChatRoom(roomId)
        room?.let {
            val updatedRoom = it.copy(
                lastMessage = message.message,
                lastMessageTime = message.timestamp
            )
            chatDao.updateChatRoom(updatedRoom)
        }
    }

    suspend fun markMessagesAsRead(roomId: String, currentUserId: String) = 
        chatDao.markMessagesAsRead(roomId, currentUserId)

    // Additional patient operations
    suspend fun getPatientById(id: String): Patient? = patientDao.getPatientById(id)

    // Additional diagnosis operations
    suspend fun getDiagnosisById(id: String): Diagnosis? = diagnosisDao.getDiagnosisById(id)
    
    // Doctor API operations
    suspend fun getDoctorsFromApi(): List<DoctorInfo> {
        return try {
            val response = api.getAllDoctors()
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.doctors ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    // File upload operations
    suspend fun uploadTestFile(
        patientUserId: String,
        testType: String,
        file: File,
        notes: String? = null
    ): TestUploadResponse? {
        return try {
            val patientUserIdBody = patientUserId.toRequestBody("text/plain".toMediaTypeOrNull())
            val testTypeBody = testType.toRequestBody("text/plain".toMediaTypeOrNull())
            val notesBody = notes?.toRequestBody("text/plain".toMediaTypeOrNull())
            
            val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
            
            val response = api.uploadTest(patientUserIdBody, testTypeBody, notesBody, filePart)
            if (response.isSuccessful) {
                response.body()
            } else {
                android.util.Log.e("BloodDiagRepo", "Upload failed: ${response.code()} - ${response.message()}")
                android.util.Log.e("BloodDiagRepo", "Error body: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("BloodDiagRepo", "Upload exception: ${e.message}", e)
            e.printStackTrace()
            null
        }
    }
    
    suspend fun getPatientUploads(userId: String): List<com.aiblooddiagnostics.data.api.models.TestUpload> {
        return try {
            val response = api.getPatientUploads(userId)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.uploads ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    // Connection request operations
    suspend fun createConnectionRequest(
        patientUserId: String,
        doctorUserId: String,
        testUploadId: Int? = null,
        notes: String? = null
    ): ConnectionResponse? {
        return try {
            val request = ConnectionRequest(
                patientUserId = patientUserId,
                doctorUserId = doctorUserId,
                testUploadId = testUploadId,
                notes = notes
            )
            val response = api.requestConnection(request)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    suspend fun getPatientConnections(userId: String): List<Connection> {
        return try {
            val response = api.getPatientConnections(userId)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.connections ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    suspend fun getDoctorConnections(userId: String): List<Connection> {
        return try {
            val response = api.getDoctorConnections(userId)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.connections ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    suspend fun approveConnection(connectionId: Int, approve: Boolean): ConnectionResponse? {
        return try {
            val status = if (approve) "approved" else "rejected"
            val request = ApproveConnectionRequest(connectionId, status)
            val response = api.approveConnection(request)
            response.body()
        } catch (e: Exception) {
            android.util.Log.e("BloodDiagRepo", "Approve connection error: ${e.message}", e)
            e.printStackTrace()
            null
        }
    }
    
    suspend fun getChatRooms(userId: String, userType: String): List<ChatRoomData> {
        return try {
            val response = api.getChatRooms(userId, userType)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.rooms ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    suspend fun getChatMessages(roomId: String): List<ChatMessageData> {
        return try {
            val response = api.getChatMessages(roomId)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.messages ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    suspend fun sendChatMessage(
        roomId: String,
        senderId: Int,
        senderType: String,
        receiverId: Int,
        receiverType: String,
        message: String
    ): SendMessageResponse? {
        return try {
            val request = SendMessageRequest(roomId, senderId, senderType, receiverId, receiverType, message)
            val response = api.sendMessage(request)
            response.body()
        } catch (e: Exception) {
            android.util.Log.e("BloodDiagRepo", "Send message error: ${e.message}", e)
            e.printStackTrace()
            null
        }
    }
}