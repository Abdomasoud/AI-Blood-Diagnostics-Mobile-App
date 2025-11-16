package com.aiblooddiagnostics.routes

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.sql.ResultSet
import java.util.UUID

@Serializable
data class TestUploadRequest(
    val patientUserId: String,
    val testType: String, // CBC, MSI, Both
    val notes: String? = null
)

@Serializable
data class TestUploadResponse(
    val success: Boolean,
    val message: String,
    val uploadId: Int? = null,
    val fileName: String? = null
)

@Serializable
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

@Serializable
data class ConnectionRequest(
    val patientUserId: String,
    val doctorUserId: String,
    val testUploadId: Int? = null,
    val notes: String? = null
)

@Serializable
data class ConnectionResponse(
    val success: Boolean,
    val message: String,
    val connectionId: Int? = null
)

@Serializable
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

@Serializable
data class PatientConnectionsResponse(
    val success: Boolean,
    val connections: List<Connection>,
    val message: String? = null
)

@Serializable
data class ApproveConnectionRequest(
    val connectionId: Int,
    val status: String // "approved" or "rejected"
)

@Serializable
data class ChatRoom(
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

@Serializable
data class ChatMessage(
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

@Serializable
data class SendMessageRequest(
    val roomId: String,
    val senderId: Int,
    val senderType: String,
    val receiverId: Int,
    val receiverType: String,
    val message: String
)

@Serializable
data class ChatRoomsResponse(
    val success: Boolean,
    val rooms: List<ChatRoom>,
    val message: String? = null
)

@Serializable
data class ChatMessagesResponse(
    val success: Boolean,
    val messages: List<ChatMessage>,
    val message: String? = null
)

fun Route.patientRoutes() {
    route("/api/patients") {
        get("/{id}") {
            call.respondText("Get patient by ID - TODO")
        }
    }
}

fun Route.connectionRoutes() {
    route("/api/connections") {
        // Create a connection request (patient requests to connect with doctor)
        post {
            val request = call.receive<ConnectionRequest>()
            
            val response = transaction {
                try {
                    // Get patient ID from user_id
                    var patientId: Int? = null
                    exec("SELECT id FROM patients WHERE user_id = '${request.patientUserId}'") { rs: ResultSet ->
                        if (rs.next()) {
                            patientId = rs.getInt("id")
                        }
                    }
                    
                    if (patientId == null) {
                        return@transaction ConnectionResponse(success = false, message = "Patient not found")
                    }
                    
                    // Get doctor ID from user_id
                    var doctorId: Int? = null
                    exec("SELECT id FROM doctors WHERE user_id = '${request.doctorUserId}'") { rs: ResultSet ->
                        if (rs.next()) {
                            doctorId = rs.getInt("id")
                        }
                    }
                    
                    if (doctorId == null) {
                        return@transaction ConnectionResponse(success = false, message = "Doctor not found")
                    }
                    
                    // Check if connection already exists
                    var exists = false
                    exec("SELECT id FROM doctor_patient_connections WHERE patient_id = $patientId AND doctor_id = $doctorId") { rs: ResultSet ->
                        if (rs.next()) {
                            exists = true
                        }
                    }
                    
                    if (exists) {
                        return@transaction ConnectionResponse(success = false, message = "Connection request already exists")
                    }
                    
                    // Create connection
                    var connectionId: Int? = null
                    
                    // Insert the connection with test upload link
                    exec("""
                        INSERT INTO doctor_patient_connections (patient_id, doctor_id, test_upload_id, status, notes)
                        VALUES ($patientId, $doctorId, ${request.testUploadId ?: "NULL"}, 'pending', ${request.notes?.let { "'${it.replace("'", "''")}'" } ?: "NULL"})
                    """)
                    
                    // Get the last inserted ID
                    exec("SELECT lastval()") { rs: ResultSet ->
                        if (rs.next()) {
                            connectionId = rs.getInt(1)
                        }
                    }
                    
                    ConnectionResponse(
                        success = true,
                        message = "Connection request sent successfully",
                        connectionId = connectionId
                    )
                } catch (e: Exception) {
                    application.log.error("Error creating connection: ${e.message}", e)
                    ConnectionResponse(success = false, message = "Failed to create connection: ${e.message}")
                }
            }
            
            call.respond(if (response.success) HttpStatusCode.Created else HttpStatusCode.BadRequest, response)
        }
        
        // Get patient's connections
        get("/patient/{userId}") {
            val userId = call.parameters["userId"]
            
            val response = transaction {
                try {
                    val connections = mutableListOf<Connection>()
                    val query = """
                        SELECT c.id, c.patient_id, p.full_name as patient_name,
                               c.doctor_id, d.full_name as doctor_name,
                               c.status, c.request_date, c.test_upload_id, c.notes
                        FROM doctor_patient_connections c
                        JOIN patients p ON c.patient_id = p.id
                        JOIN doctors d ON c.doctor_id = d.id
                        WHERE p.user_id = '$userId'
                        ORDER BY c.request_date DESC
                    """
                    
                    exec(query) { rs: ResultSet ->
                        while (rs.next()) {
                            connections.add(
                                Connection(
                                    id = rs.getInt("id"),
                                    patientId = rs.getInt("patient_id"),
                                    patientName = rs.getString("patient_name"),
                                    doctorId = rs.getInt("doctor_id"),
                                    doctorName = rs.getString("doctor_name"),
                                    status = rs.getString("status"),
                                    requestDate = rs.getTimestamp("request_date").toString(),
                                    testUploadId = rs.getObject("test_upload_id") as? Int,
                                    notes = rs.getString("notes")
                                )
                            )
                        }
                    }
                    
                    PatientConnectionsResponse(success = true, connections = connections)
                } catch (e: Exception) {
                    application.log.error("Error fetching connections: ${e.message}", e)
                    PatientConnectionsResponse(success = false, connections = emptyList(), message = e.message)
                }
            }
            
            call.respond(HttpStatusCode.OK, response)
        }
        
        // Get doctor's connection requests
        get("/doctor/{userId}") {
            val userId = call.parameters["userId"]
            
            val response = transaction {
                try {
                    val connections = mutableListOf<Connection>()
                    val query = """
                        SELECT c.id, c.patient_id, p.full_name as patient_name,
                               c.doctor_id, d.full_name as doctor_name,
                               c.status, c.request_date, c.test_upload_id, c.notes
                        FROM doctor_patient_connections c
                        JOIN patients p ON c.patient_id = p.id
                        JOIN doctors d ON c.doctor_id = d.id
                        WHERE d.user_id = '$userId'
                        ORDER BY c.request_date DESC
                    """
                    
                    exec(query) { rs: ResultSet ->
                        while (rs.next()) {
                            connections.add(
                                Connection(
                                    id = rs.getInt("id"),
                                    patientId = rs.getInt("patient_id"),
                                    patientName = rs.getString("patient_name"),
                                    doctorId = rs.getInt("doctor_id"),
                                    doctorName = rs.getString("doctor_name"),
                                    status = rs.getString("status"),
                                    requestDate = rs.getTimestamp("request_date").toString(),
                                    testUploadId = rs.getObject("test_upload_id") as? Int,
                                    notes = rs.getString("notes")
                                )
                            )
                        }
                    }
                    
                    PatientConnectionsResponse(success = true, connections = connections)
                } catch (e: Exception) {
                    application.log.error("Error fetching doctor connections: ${e.message}", e)
                    PatientConnectionsResponse(success = false, connections = emptyList(), message = e.message)
                }
            }
            
            call.respond(HttpStatusCode.OK, response)
        }
        
        // Approve or reject connection request
        post("/approve") {
            val request = call.receive<ApproveConnectionRequest>()
            
            val response = transaction {
                try {
                    // Update connection status
                    exec("""
                        UPDATE doctor_patient_connections 
                        SET status = '${request.status}', approval_date = CURRENT_TIMESTAMP
                        WHERE id = ${request.connectionId}
                    """)
                    
                    if (request.status == "approved") {
                        // Get connection details including patient's notes
                        var doctorId: Int? = null
                        var patientId: Int? = null
                        var notes: String? = null
                        
                        exec("SELECT doctor_id, patient_id, notes FROM doctor_patient_connections WHERE id = ${request.connectionId}") { rs ->
                            if (rs.next()) {
                                doctorId = rs.getInt("doctor_id")
                                patientId = rs.getInt("patient_id")
                                notes = rs.getString("notes")
                            }
                        }
                        
                        if (doctorId != null && patientId != null) {
                            // Create chat room
                            val roomId = "doctor_${doctorId}_patient_${patientId}"
                            
                            exec("""
                                INSERT INTO chat_rooms (room_id, doctor_id, patient_id, connection_id)
                                VALUES ('$roomId', $doctorId, $patientId, ${request.connectionId})
                                ON CONFLICT (doctor_id, patient_id) DO NOTHING
                            """)
                            
                            // Insert patient's initial message if there's a note
                            val patientNotes = notes
                            if (patientNotes != null && patientNotes.isNotBlank()) {
                                val escapedNotes = patientNotes.replace("'", "''")
                                exec("""
                                    INSERT INTO chat_messages (room_id, sender_id, sender_type, receiver_id, receiver_type, message, created_at)
                                    VALUES ('$roomId', $patientId, 'patient', $doctorId, 'doctor', '$escapedNotes', CURRENT_TIMESTAMP)
                                """)
                                
                                // Update last message in chat room
                                exec("""
                                    UPDATE chat_rooms 
                                    SET last_message = '$escapedNotes', 
                                        last_message_time = CURRENT_TIMESTAMP 
                                    WHERE room_id = '$roomId'
                                """)
                            }
                        }
                    }
                    
                    ConnectionResponse(success = true, message = "Connection ${request.status} successfully")
                } catch (e: Exception) {
                    application.log.error("Error approving connection: ${e.message}", e)
                    ConnectionResponse(success = false, message = "Failed to update connection: ${e.message}")
                }
            }
            
            call.respond(if (response.success) HttpStatusCode.OK else HttpStatusCode.BadRequest, response)
        }
    }
}

fun Route.testUploadRoutes() {
    route("/api/uploads") {
        // Upload a test file
        post {
            var uploadResponse: TestUploadResponse? = null
            
            val multipart = call.receiveMultipart()
            var patientUserId: String? = null
            var testType: String? = null
            var notes: String? = null
            var fileName: String? = null
            var fileBytes: ByteArray? = null
            var fileSize: Long = 0
            
            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        when (part.name) {
                            "patientUserId" -> patientUserId = part.value
                            "testType" -> testType = part.value
                            "notes" -> notes = part.value
                        }
                    }
                    is PartData.FileItem -> {
                        fileName = part.originalFileName ?: "unknown"
                        fileBytes = part.streamProvider().readBytes()
                        fileSize = fileBytes?.size?.toLong() ?: 0
                    }
                    else -> {}
                }
                part.dispose()
            }
            
            if (patientUserId == null || testType == null || fileBytes == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    TestUploadResponse(success = false, message = "Missing required fields")
                )
                return@post
            }
            
            uploadResponse = transaction {
                try {
                    // Get patient ID
                    var patientId: Int? = null
                    exec("SELECT id FROM patients WHERE user_id = '$patientUserId'") { rs: ResultSet ->
                        if (rs.next()) {
                            patientId = rs.getInt("id")
                        }
                    }
                    
                    if (patientId == null) {
                        return@transaction TestUploadResponse(success = false, message = "Patient not found")
                    }
                    
                    // Save file to disk
                    val uploadDir = File("uploads")
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs()
                    }
                    
                    val uniqueFileName = "${UUID.randomUUID()}_$fileName"
                    val file = File(uploadDir, uniqueFileName)
                    file.writeBytes(fileBytes!!)
                    
                    // Create immutable copies for use in SQL
                    val finalFileName = fileName!!
                    val finalFilePath = file.absolutePath
                    val fileExtension = finalFileName.substringAfterLast('.', "")
                    val fileType = when (fileExtension.lowercase()) {
                        "pdf", "doc", "docx" -> "Document"
                        "jpg", "jpeg", "png", "bmp" -> "Image"
                        else -> "Unknown"
                    }
                    
                    // Save to database
                    var uploadId: Int? = null
                    
                    // Insert the upload
                    exec("""
                        INSERT INTO test_uploads (patient_id, test_type, file_type, file_name, file_path, file_size, status, notes)
                        VALUES ($patientId, '$testType', '$fileType', '${finalFileName.replace("'", "''")}', '${finalFilePath.replace("'", "''")}', $fileSize, 'pending', ${notes?.let { "'${it.replace("'", "''")}'" } ?: "NULL"})
                    """)
                    
                    // Get the last inserted ID
                    exec("SELECT lastval()") { rs: ResultSet ->
                        if (rs.next()) {
                            uploadId = rs.getInt(1)
                        }
                    }
                    
                    TestUploadResponse(
                        success = true,
                        message = "File uploaded successfully",
                        uploadId = uploadId,
                        fileName = uniqueFileName
                    )
                } catch (e: Exception) {
                    application.log.error("Error uploading file: ${e.message}", e)
                    TestUploadResponse(success = false, message = "Upload failed: ${e.message}")
                }
            }
            
            call.respond(
                if (uploadResponse.success) HttpStatusCode.Created else HttpStatusCode.InternalServerError,
                uploadResponse
            )
        }
        
        // Get patient's uploads
        get("/patient/{userId}") {
            val userId = call.parameters["userId"]
            
            val response = transaction {
                try {
                    val uploads = mutableListOf<TestUpload>()
                    val query = """
                        SELECT t.id, t.patient_id, t.test_type, t.file_type, t.file_name,
                               t.file_path, t.file_size, t.upload_date, t.status, t.notes
                        FROM test_uploads t
                        JOIN patients p ON t.patient_id = p.id
                        WHERE p.user_id = '$userId'
                        ORDER BY t.upload_date DESC
                    """
                    
                    exec(query) { rs: ResultSet ->
                        while (rs.next()) {
                            uploads.add(
                                TestUpload(
                                    id = rs.getInt("id"),
                                    patientId = rs.getInt("patient_id"),
                                    testType = rs.getString("test_type"),
                                    fileType = rs.getString("file_type"),
                                    fileName = rs.getString("file_name"),
                                    filePath = rs.getString("file_path"),
                                    fileSize = rs.getLong("file_size"),
                                    uploadDate = rs.getTimestamp("upload_date").toString(),
                                    status = rs.getString("status"),
                                    notes = rs.getString("notes")
                                )
                            )
                        }
                    }
                    
                    mapOf("success" to true, "uploads" to uploads)
                } catch (e: Exception) {
                    application.log.error("Error fetching uploads: ${e.message}", e)
                    mapOf("success" to false, "uploads" to emptyList<TestUpload>(), "message" to e.message)
                }
            }
            
            call.respond(HttpStatusCode.OK, response)
        }
    }
}

fun Route.chatRoutes() {
    route("/api/chat") {
        // Get chat rooms for a user
        get("/rooms/{userId}/{userType}") {
            val userId = call.parameters["userId"]
            val userType = call.parameters["userType"] // "doctor" or "patient"
            
            val response = transaction {
                try {
                    val rooms = mutableListOf<ChatRoom>()
                    // Optimized query: filter user first in subquery, then join only needed rows
                    val query = if (userType == "doctor") {
                        """
                        SELECT cr.id, cr.room_id, cr.doctor_id, d.full_name as doctor_name,
                               cr.patient_id, p.full_name as patient_name,
                               cr.last_message, cr.last_message_time, cr.connection_id
                        FROM chat_rooms cr
                        INNER JOIN doctors d ON cr.doctor_id = d.id AND d.user_id = '$userId'
                        INNER JOIN patients p ON cr.patient_id = p.id
                        ORDER BY cr.last_message_time DESC NULLS LAST
                        """
                    } else {
                        """
                        SELECT cr.id, cr.room_id, cr.doctor_id, d.full_name as doctor_name,
                               cr.patient_id, p.full_name as patient_name,
                               cr.last_message, cr.last_message_time, cr.connection_id
                        FROM chat_rooms cr
                        INNER JOIN doctors d ON cr.doctor_id = d.id
                        INNER JOIN patients p ON cr.patient_id = p.id AND p.user_id = '$userId'
                        ORDER BY cr.last_message_time DESC NULLS LAST
                        """
                    }
                    
                    exec(query) { rs: ResultSet ->
                        while (rs.next()) {
                            rooms.add(
                                ChatRoom(
                                    id = rs.getInt("id"),
                                    roomId = rs.getString("room_id"),
                                    doctorId = rs.getInt("doctor_id"),
                                    doctorName = rs.getString("doctor_name"),
                                    patientId = rs.getInt("patient_id"),
                                    patientName = rs.getString("patient_name"),
                                    lastMessage = rs.getString("last_message"),
                                    lastMessageTime = rs.getTimestamp("last_message_time")?.toString(),
                                    connectionId = rs.getObject("connection_id") as? Int
                                )
                            )
                        }
                    }
                    
                    ChatRoomsResponse(success = true, rooms = rooms)
                } catch (e: Exception) {
                    application.log.error("Error fetching chat rooms: ${e.message}", e)
                    ChatRoomsResponse(success = false, rooms = emptyList(), message = e.message)
                }
            }
            
            call.respond(HttpStatusCode.OK, response)
        }
        
        // Get messages for a room
        get("/messages/{roomId}") {
            val roomId = call.parameters["roomId"]
            
            val response = transaction {
                try {
                    val messages = mutableListOf<ChatMessage>()
                    val query = """
                        SELECT id, room_id, sender_id, sender_type, receiver_id, receiver_type,
                               message, is_read, created_at
                        FROM chat_messages
                        WHERE room_id = '$roomId'
                        ORDER BY created_at ASC
                    """
                    
                    exec(query) { rs: ResultSet ->
                        while (rs.next()) {
                            messages.add(
                                ChatMessage(
                                    id = rs.getInt("id"),
                                    roomId = rs.getString("room_id"),
                                    senderId = rs.getInt("sender_id"),
                                    senderType = rs.getString("sender_type"),
                                    receiverId = rs.getInt("receiver_id"),
                                    receiverType = rs.getString("receiver_type"),
                                    message = rs.getString("message"),
                                    isRead = rs.getBoolean("is_read"),
                                    createdAt = rs.getTimestamp("created_at").toString()
                                )
                            )
                        }
                    }
                    
                    ChatMessagesResponse(success = true, messages = messages)
                } catch (e: Exception) {
                    application.log.error("Error fetching messages: ${e.message}", e)
                    ChatMessagesResponse(success = false, messages = emptyList(), message = e.message)
                }
            }
            
            call.respond(HttpStatusCode.OK, response)
        }
        
        // Send a message
        post("/send") {
            val request = call.receive<SendMessageRequest>()
            
            val response = transaction {
                try {
                    val messageSafe = request.message.replace("'", "''")
                    
                    // Insert message
                    exec("""
                        INSERT INTO chat_messages (room_id, sender_id, sender_type, receiver_id, receiver_type, message)
                        VALUES ('${request.roomId}', ${request.senderId}, '${request.senderType}', ${request.receiverId}, '${request.receiverType}', '$messageSafe')
                    """)
                    
                    // Update chat room last message
                    exec("""
                        UPDATE chat_rooms 
                        SET last_message = '$messageSafe', last_message_time = CURRENT_TIMESTAMP
                        WHERE room_id = '${request.roomId}'
                    """)
                    
                    mapOf("success" to true, "message" to "Message sent successfully")
                } catch (e: Exception) {
                    application.log.error("Error sending message: ${e.message}", e)
                    mapOf("success" to false, "message" to "Failed to send message: ${e.message}")
                }
            }
            
            call.respond(if (response["success"] == true) HttpStatusCode.OK else HttpStatusCode.BadRequest, response)
        }
    }
}

fun Route.diagnosisRoutes() {
    route("/api/diagnosis") {
        get {
            call.respondText("Get diagnoses - TODO")
        }
    }
}
