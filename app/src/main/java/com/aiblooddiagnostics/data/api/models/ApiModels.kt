package com.aiblooddiagnostics.data.api.models

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String,
    val userType: String // "doctor" or "patient"
)

data class SignupRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val userType: String, // "doctor" or "patient"
    val mobileNumber: String? = null,
    val specialization: String? = null,
    val experienceYears: Int? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val bloodType: String? = null
)

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val userId: String? = null,
    val userType: String? = null,
    val fullName: String? = null,
    val email: String? = null
)

data class DoctorResponse(
    val id: Int,
    val userId: String,
    val fullName: String,
    val email: String,
    val specialization: String,
    val experienceYears: Int,
    val rating: Double,
    val bio: String?
)

data class PatientResponse(
    val id: Int,
    val userId: String,
    val fullName: String,
    val email: String,
    val dateOfBirth: String?,
    val gender: String?,
    val bloodType: String?,
    val medicalHistory: String?
)

data class ConnectionRequest(
    val patientUserId: String,
    val doctorUserId: String,
    val testUploadId: Int? = null,
    val notes: String? = null
)

data class ConnectionStatusUpdate(
    val status: String, // "approved" or "rejected"
    val approvalDate: Long? = null
)

data class ConnectionResponse(
    val success: Boolean,
    val message: String,
    val connectionId: Int? = null
)

data class Connection(
    val id: Int,
    val patientId: Int,
    val patientName: String,
    val doctorId: Int,
    val doctorName: String,
    val status: String,
    val requestDate: String,
    val testUploadId: Int?,
    val notes: String?
)

data class PatientConnectionsResponse(
    val success: Boolean,
    val connections: List<Connection>,
    val message: String? = null
)

data class DoctorInfo(
    val id: Int,
    val userId: String,
    val fullName: String,
    val email: String,
    val specialization: String,
    val experienceYears: Int,
    val rating: Double,
    val bio: String?
)

data class DoctorListResponse(
    val success: Boolean,
    val doctors: List<DoctorInfo>,
    val message: String? = null
)

data class TestUploadRequest(
    val patientId: String,
    val testType: String,
    val fileType: String,
    val fileName: String,
    val filePath: String,
    val fileSize: Long,
    val notes: String? = null
)

data class TestUploadResponse(
    val success: Boolean,
    val message: String,
    val uploadId: Int? = null,
    val fileName: String? = null
)

data class TestUpload(
    val id: Int,
    val patientId: Int,
    val testType: String,
    val fileType: String,
    val fileName: String,
    val filePath: String,
    val fileSize: Long,
    val uploadDate: String,
    val status: String,
    val notes: String?
)

data class TestUploadsResponse(
    val success: Boolean,
    val uploads: List<TestUpload>,
    val message: String? = null
)

data class ChatMessageRequest(
    val senderId: String,
    val senderType: String,
    val receiverId: String,
    val receiverType: String,
    val message: String
)

data class ChatMessageResponse(
    val id: Int,
    val senderId: String,
    val senderType: String,
    val receiverId: String,
    val receiverType: String,
    val message: String,
    val isRead: Boolean,
    val createdAt: String
)

data class DiagnosisResponse(
    val id: Int,
    val patientId: String,
    val doctorId: String,
    val diagnosisType: String,
    val result: String,
    val recommendations: String?,
    val createdAt: String
)

data class ApproveConnectionRequest(
    val connectionId: Int,
    val status: String // "approved" or "rejected"
)

data class ChatRoomData(
    val id: Int,
    val roomId: String,
    val doctorId: Int,
    val doctorName: String,
    val patientId: Int,
    val patientName: String,
    val lastMessage: String?,
    val lastMessageTime: String?,
    val connectionId: Int?
)

data class ChatRoomsResponse(
    val success: Boolean,
    val rooms: List<ChatRoomData>,
    val message: String? = null
)

data class ChatMessageData(
    val id: Int,
    val roomId: String,
    val senderId: Int,
    val senderType: String,
    val receiverId: Int,
    val receiverType: String,
    val message: String,
    val isRead: Boolean,
    val createdAt: String
)

data class ChatMessagesResponse(
    val success: Boolean,
    val messages: List<ChatMessageData>,
    val message: String? = null
)

data class SendMessageRequest(
    val roomId: String,
    val senderId: Int,
    val senderType: String,
    val receiverId: Int,
    val receiverType: String,
    val message: String
)

data class SendMessageResponse(
    val success: Boolean,
    val message: String
)
