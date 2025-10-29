package com.aiblooddiagnostics.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiblooddiagnostics.data.model.ChatMessage
import com.aiblooddiagnostics.data.model.ChatRoom
import com.aiblooddiagnostics.data.repository.BloodDiagnosticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: BloodDiagnosticsRepository
) : ViewModel() {

    private val _currentUserId = MutableStateFlow<String?>(null)
    private val _currentUserType = MutableStateFlow<String?>(null)
    private val _selectedChatRoom = MutableStateFlow<String?>(null)

    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()
    val currentUserType: StateFlow<String?> = _currentUserType.asStateFlow()

    val chatRooms = _currentUserId.flatMapLatest { userId ->
        if (userId != null && _currentUserType.value == "doctor") {
            repository.getChatRoomsForDoctor(userId)
        } else if (userId != null && _currentUserType.value == "patient") {
            repository.getChatRoomsForPatient(userId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val messages = _selectedChatRoom.flatMapLatest { roomId ->
        if (roomId != null) {
            repository.getMessagesForRoom(roomId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setCurrentUser(userId: String, userType: String) {
        _currentUserId.value = userId
        _currentUserType.value = userType
    }

    fun selectChatRoom(roomId: String) {
        _selectedChatRoom.value = roomId
        // Mark messages as read
        _currentUserId.value?.let { userId ->
            viewModelScope.launch {
                repository.markMessagesAsRead(roomId, userId)
            }
        }
    }

    fun sendMessage(message: String, doctorId: String, patientId: String) {
        val userId = _currentUserId.value ?: return
        val userType = _currentUserType.value ?: return
        
        viewModelScope.launch {
            val roomId = "${doctorId}_${patientId}"
            
            // Create chat room if it doesn't exist
            val existingRoom = repository.getChatRoom(roomId)
            if (existingRoom == null) {
                val newRoom = ChatRoom(
                    id = roomId,
                    doctorId = doctorId,
                    patientId = patientId,
                    lastMessage = null,
                    lastMessageTime = null
                )
                repository.createChatRoom(newRoom)
            }

            val chatMessage = ChatMessage(
                id = "msg_${System.currentTimeMillis()}",
                chatRoomId = roomId,
                senderId = userId,
                senderType = userType,
                message = message,
                timestamp = Date()
            )

            repository.sendMessage(chatMessage)
            
            // Debug: Log message sent
            println("Message sent: ${chatMessage.message} from ${chatMessage.senderId} in room ${chatMessage.chatRoomId}")
        }
    }

    fun initializeMockChatData() {
        viewModelScope.launch {
            // Create mock chat rooms and messages
            val mockChatRooms = listOf(
                ChatRoom(
                    id = "doctor_3_patient_user_1",
                    doctorId = "doctor_3",
                    patientId = "patient_user_1",
                    lastMessage = "Thank you doctor",
                    lastMessageTime = Date()
                ),
                ChatRoom(
                    id = "doctor_3_patient_user_2",
                    doctorId = "doctor_3",
                    patientId = "patient_user_2",
                    lastMessage = "When should I take the medication?",
                    lastMessageTime = Date(System.currentTimeMillis() - 3600000) // 1 hour ago
                )
            )

            mockChatRooms.forEach { room ->
                repository.createChatRoom(room)
            }

            // Create mock messages
            val mockMessages = listOf(
                // Conversation 1: Dr. Amira with Patient Ahmed
                ChatMessage(
                    id = "msg_1",
                    chatRoomId = "doctor_3_patient_user_1",
                    senderId = "doctor_3",
                    senderType = "doctor",
                    message = "Hello Ahmed, I've reviewed your blood test results.",
                    timestamp = Date(System.currentTimeMillis() - 7200000), // 2 hours ago
                    isRead = true
                ),
                ChatMessage(
                    id = "msg_2",
                    chatRoomId = "doctor_3_patient_user_1",
                    senderId = "patient_user_1",
                    senderType = "patient",
                    message = "Hello Doctor, what do the results show?",
                    timestamp = Date(System.currentTimeMillis() - 7000000),
                    isRead = true
                ),
                ChatMessage(
                    id = "msg_3",
                    chatRoomId = "doctor_3_patient_user_1",
                    senderId = "doctor_3",
                    senderType = "doctor",
                    message = "Your CBC results show slight anemia. Your hemoglobin is 11.2 g/dL, which is below normal range. I recommend iron supplements and dietary changes.",
                    timestamp = Date(System.currentTimeMillis() - 6800000),
                    isRead = true
                ),
                ChatMessage(
                    id = "msg_4",
                    chatRoomId = "doctor_3_patient_user_1",
                    senderId = "patient_user_1",
                    senderType = "patient",
                    message = "What foods should I eat to help with the anemia?",
                    timestamp = Date(System.currentTimeMillis() - 6600000),
                    isRead = true
                ),
                ChatMessage(
                    id = "msg_5",
                    chatRoomId = "doctor_3_patient_user_1",
                    senderId = "doctor_3",
                    senderType = "doctor",
                    message = "Include iron-rich foods like spinach, red meat, lentils, and fortified cereals. Also, take vitamin C with iron for better absorption.",
                    timestamp = Date(System.currentTimeMillis() - 6400000),
                    isRead = true
                ),
                ChatMessage(
                    id = "msg_6",
                    chatRoomId = "doctor_3_patient_user_1",
                    senderId = "patient_user_1",
                    senderType = "patient",
                    message = "Thank you doctor",
                    timestamp = Date(System.currentTimeMillis() - 6200000),
                    isRead = true
                ),

                // Conversation 2: Dr. Amira with Sarah Mohamed
                ChatMessage(
                    id = "msg_7",
                    chatRoomId = "doctor_3_patient_user_2",
                    senderId = "doctor_3",
                    senderType = "doctor",
                    message = "Hi Sarah, your test results are ready. Overall looking good!",
                    timestamp = Date(System.currentTimeMillis() - 4000000), // Different time
                    isRead = true
                ),
                ChatMessage(
                    id = "msg_8",
                    chatRoomId = "doctor_3_patient_user_2",
                    senderId = "patient_user_2",
                    senderType = "patient",
                    message = "That's great to hear! Are there any medications I need to take?",
                    timestamp = Date(System.currentTimeMillis() - 3800000),
                    isRead = true
                ),
                ChatMessage(
                    id = "msg_9",
                    chatRoomId = "doctor_3_patient_user_2",
                    senderId = "doctor_3",
                    senderType = "doctor",
                    message = "I'll prescribe a multivitamin. Take one daily with breakfast for the next 3 months.",
                    timestamp = Date(System.currentTimeMillis() - 3600000),
                    isRead = true
                ),
                ChatMessage(
                    id = "msg_10",
                    chatRoomId = "doctor_3_patient_user_2",
                    senderId = "patient_user_2",
                    senderType = "patient",
                    message = "When should I take the medication?",
                    timestamp = Date(System.currentTimeMillis() - 3400000),
                    isRead = false
                )
            )

            mockMessages.forEach { message ->
                repository.sendMessage(message)
            }
        }
    }
}