package com.aiblooddiagnostics.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiblooddiagnostics.data.api.models.ChatMessageData
import com.aiblooddiagnostics.data.api.models.ChatRoomData
import com.aiblooddiagnostics.data.repository.BloodDiagnosticsRepository
import com.aiblooddiagnostics.data.manager.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: BloodDiagnosticsRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _chatRooms = MutableStateFlow<List<ChatRoomData>>(emptyList())
    val chatRooms: StateFlow<List<ChatRoomData>> = _chatRooms.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessageData>>(emptyList())
    val messages: StateFlow<List<ChatMessageData>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentTestUploadId = MutableStateFlow<String?>(null)
    val currentTestUploadId: StateFlow<String?> = _currentTestUploadId.asStateFlow()

    // Cache to prevent unnecessary reloading
    private var cachedUserId: String? = null
    private var cachedUserType: String? = null

    fun loadChatRooms(userType: String, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                val userId = sessionManager.getUserId()
                
                // Skip loading if already loaded for this user and not forcing refresh
                if (!forceRefresh && userId == cachedUserId && userType == cachedUserType && _chatRooms.value.isNotEmpty()) {
                    android.util.Log.d("ChatViewModel", "Using cached chat rooms")
                    return@launch
                }
                
                _isLoading.value = true
                if (userId != null) {
                    android.util.Log.d("ChatViewModel", "Loading chat rooms for user: $userId, type: $userType")
                    val rooms = repository.getChatRooms(userId, userType)
                    _chatRooms.value = rooms ?: emptyList()
                    cachedUserId = userId
                    cachedUserType = userType
                    android.util.Log.d("ChatViewModel", "Loaded ${_chatRooms.value.size} chat rooms")
                }
            } catch (e: Exception) {
                android.util.Log.e("ChatViewModel", "Error loading chat rooms", e)
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMessages(roomId: String, testUploadId: String? = null) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _currentTestUploadId.value = testUploadId
                android.util.Log.d("ChatViewModel", "Loading messages for room: $roomId")
                val messageList = repository.getChatMessages(roomId)
                _messages.value = messageList ?: emptyList()
                android.util.Log.d("ChatViewModel", "Loaded ${_messages.value.size} messages")
            } catch (e: Exception) {
                android.util.Log.e("ChatViewModel", "Error loading messages", e)
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sendMessage(
        roomId: String,
        messageText: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val userId = sessionManager.getUserId()
                val userType = sessionManager.getUserType()
                
                if (userId == null || userType == null) {
                    onResult(false, "User not logged in")
                    return@launch
                }

                android.util.Log.d("ChatViewModel", "Sending message in room: $roomId")
                // Extract doctor and patient IDs from roomId format: doctor_{doctorId}_patient_{patientId}
                val parts = roomId.split("_")
                val doctorId = parts.getOrNull(1)?.toIntOrNull() ?: 0
                val patientId = parts.getOrNull(3)?.toIntOrNull() ?: 0
                
                val senderId = userId.replace("doctor_", "").replace("patient_user_", "").toIntOrNull() ?: 0
                val receiverId = if (userType == "doctor") patientId else doctorId
                val receiverType = if (userType == "doctor") "patient" else "doctor"
                
                val response = repository.sendChatMessage(
                    roomId = roomId,
                    senderId = senderId,
                    senderType = userType,
                    receiverId = receiverId,
                    receiverType = receiverType,
                    message = messageText
                )

                if (response?.success == true) {
                    // Reload messages to show the new one
                    loadMessages(roomId, _currentTestUploadId.value)
                    onResult(true, null)
                } else {
                    onResult(false, response?.message ?: "Failed to send message")
                }
            } catch (e: Exception) {
                android.util.Log.e("ChatViewModel", "Error sending message", e)
                e.printStackTrace()
                onResult(false, e.message ?: "Unknown error")
            }
        }
    }

    fun refreshMessages(roomId: String) {
        loadMessages(roomId, _currentTestUploadId.value)
    }
}
