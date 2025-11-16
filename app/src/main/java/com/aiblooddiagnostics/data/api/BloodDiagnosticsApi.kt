package com.aiblooddiagnostics.data.api

import com.aiblooddiagnostics.data.api.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface BloodDiagnosticsApi {
    
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    @POST("api/auth/signup")
    suspend fun signup(@Body request: SignupRequest): Response<AuthResponse>
    
    @GET("api/doctors")
    suspend fun getAllDoctors(): Response<DoctorListResponse>
    
    @GET("api/doctors/{id}")
    suspend fun getDoctorById(@Path("id") id: String): Response<DoctorInfo>
    
    @GET("api/patients/{id}")
    suspend fun getPatientById(@Path("id") id: String): Response<PatientResponse>
    
    @GET("api/connections/patient/{userId}")
    suspend fun getPatientConnections(@Path("userId") userId: String): Response<PatientConnectionsResponse>
    
    @GET("api/connections/doctor/{userId}")
    suspend fun getDoctorConnections(@Path("userId") userId: String): Response<PatientConnectionsResponse>
    
    @POST("api/connections")
    suspend fun requestConnection(@Body request: ConnectionRequest): Response<ConnectionResponse>
    
    @POST("api/connections/approve")
    suspend fun approveConnection(@Body request: ApproveConnectionRequest): Response<ConnectionResponse>
    
    @Multipart
    @POST("api/uploads")
    suspend fun uploadTest(
        @Part("patientUserId") patientUserId: RequestBody,
        @Part("testType") testType: RequestBody,
        @Part("notes") notes: RequestBody?,
        @Part file: MultipartBody.Part
    ): Response<TestUploadResponse>
    
    @GET("api/uploads/patient/{userId}")
    suspend fun getPatientUploads(@Path("userId") userId: String): Response<TestUploadsResponse>
    
    @GET("api/chat/rooms/{userId}/{userType}")
    suspend fun getChatRooms(
        @Path("userId") userId: String,
        @Path("userType") userType: String
    ): Response<ChatRoomsResponse>
    
    @GET("api/chat/messages/{roomId}")
    suspend fun getChatMessages(@Path("roomId") roomId: String): Response<ChatMessagesResponse>
    
    @POST("api/chat/send")
    suspend fun sendMessage(@Body request: SendMessageRequest): Response<SendMessageResponse>
    
    @GET("api/diagnosis")
    suspend fun getDiagnoses(@Query("patientId") patientId: String): Response<List<DiagnosisResponse>>
}
