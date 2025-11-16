package com.aiblooddiagnostics.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.aiblooddiagnostics.ui.viewmodel.ChatViewModel
import com.aiblooddiagnostics.data.api.models.ChatRoomData
import com.aiblooddiagnostics.ui.theme.Blue60
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    userType: String, // "patient" or "doctor"
    onNavigateBack: () -> Unit,
    onChatRoomClick: (String, String, String) -> Unit, // roomId, otherUserName, testUploadId?
    viewModel: ChatViewModel = hiltViewModel()
) {
    val chatRooms by viewModel.chatRooms.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(userType) {
        viewModel.loadChatRooms(userType, forceRefresh = false)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Chats") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                chatRooms.isEmpty() -> {
                    Text(
                        text = if (userType == "patient") 
                            "No chats yet. Request a connection with a doctor first!" 
                        else 
                            "No patient connections yet.",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(chatRooms) { room ->
                            ChatRoomItem(
                                room = room,
                                userType = userType,
                                onClick = {
                                    val otherUserName = if (userType == "patient") 
                                        room.doctorName 
                                    else 
                                        room.patientName
                                    onChatRoomClick(room.roomId, otherUserName, room.connectionId?.toString() ?: "")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatRoomItem(
    room: ChatRoomData,
    userType: String,
    onClick: () -> Unit
) {
    val otherUserName = if (userType == "patient") room.doctorName else room.patientName
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                // Show doctor image if it's a patient viewing the chat
                if (userType == "patient" && room.doctorProfileImageUrl != null && room.doctorProfileImageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = room.doctorProfileImageUrl,
                        contentDescription = "Doctor Avatar",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Blue60,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Chat info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = otherUserName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                if (room.lastMessage != null) {
                    Text(
                        text = room.lastMessage,
                        color = Color.Gray,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    Text(
                        text = "No messages yet",
                        color = Color.Gray.copy(alpha = 0.6f),
                        fontSize = 14.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Timestamp
            room.lastMessageTime?.let { timestamp ->
                Text(
                    text = timestamp,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}
