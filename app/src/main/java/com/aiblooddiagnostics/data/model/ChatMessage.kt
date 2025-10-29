package com.aiblooddiagnostics.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey
    val id: String,
    val chatRoomId: String, // combination of doctorId_patientId
    val senderId: String,
    val senderType: String, // "doctor" or "patient"
    val message: String,
    val timestamp: Date,
    val isRead: Boolean = false
)

@Entity(tableName = "chat_rooms")
data class ChatRoom(
    @PrimaryKey
    val id: String, // doctorId_patientId
    val doctorId: String,
    val patientId: String,
    val lastMessage: String?,
    val lastMessageTime: Date?,
    val unreadCount: Int = 0
)