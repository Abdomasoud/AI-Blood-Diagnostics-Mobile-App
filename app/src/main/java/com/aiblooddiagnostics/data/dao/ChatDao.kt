package com.aiblooddiagnostics.data.dao

import androidx.room.*
import com.aiblooddiagnostics.data.model.ChatMessage
import com.aiblooddiagnostics.data.model.ChatRoom
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    // Chat Room operations
    @Query("SELECT * FROM chat_rooms WHERE doctorId = :doctorId ORDER BY lastMessageTime DESC")
    fun getChatRoomsForDoctor(doctorId: String): Flow<List<ChatRoom>>

    @Query("SELECT * FROM chat_rooms WHERE patientId = :patientId ORDER BY lastMessageTime DESC")
    fun getChatRoomsForPatient(patientId: String): Flow<List<ChatRoom>>

    @Query("SELECT * FROM chat_rooms WHERE id = :roomId")
    suspend fun getChatRoom(roomId: String): ChatRoom?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatRoom(chatRoom: ChatRoom)

    @Update
    suspend fun updateChatRoom(chatRoom: ChatRoom)

    // Chat Message operations
    @Query("SELECT * FROM chat_messages WHERE chatRoomId = :roomId ORDER BY timestamp ASC")
    fun getMessagesForRoom(roomId: String): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages WHERE chatRoomId = :roomId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastMessage(roomId: String): ChatMessage?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("UPDATE chat_messages SET isRead = 1 WHERE chatRoomId = :roomId AND senderId != :currentUserId")
    suspend fun markMessagesAsRead(roomId: String, currentUserId: String)

    @Query("SELECT COUNT(*) FROM chat_messages WHERE chatRoomId = :roomId AND senderId != :currentUserId AND isRead = 0")
    suspend fun getUnreadCount(roomId: String, currentUserId: String): Int
}