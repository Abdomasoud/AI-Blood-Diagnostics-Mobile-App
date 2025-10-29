package com.aiblooddiagnostics.data.repository

import com.aiblooddiagnostics.data.dao.*
import com.aiblooddiagnostics.data.model.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BloodDiagnosticsRepository @Inject constructor(
    private val userDao: UserDao,
    private val doctorDao: DoctorDao,
    private val patientDao: PatientDao,
    private val diagnosisDao: DiagnosisDao,
    private val appointmentDao: AppointmentDao,
    private val chatDao: ChatDao
) {
    // User operations
    suspend fun loginUser(email: String, password: String): User? =
        userDao.getUserByEmailAndPassword(email, password)

    suspend fun registerUser(user: User) = userDao.insertUser(user)

    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)

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
}