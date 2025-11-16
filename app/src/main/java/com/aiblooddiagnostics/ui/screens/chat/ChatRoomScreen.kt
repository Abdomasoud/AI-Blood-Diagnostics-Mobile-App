package com.aiblooddiagnostics.ui.screens.chat

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiblooddiagnostics.ui.viewmodel.ChatViewModel
import com.aiblooddiagnostics.data.api.models.ChatMessageData
import com.aiblooddiagnostics.data.manager.SessionManager
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomScreen(
    roomId: String,
    otherUserName: String,
    testUploadId: String?,
    onNavigateBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel(),
    sessionManager: SessionManager
) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentTestUploadId by viewModel.currentTestUploadId.collectAsState()
    
    var messageText by remember { mutableStateOf("") }
    var showReportViewer by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val context = LocalContext.current

    val currentUserId = sessionManager.getUserId()
    val userType = sessionManager.getUserType()
    
    // Extract numeric ID from user ID (doctor_1 -> 1, patient_user_8 -> 8)
    val currentUserNumericId = currentUserId
        ?.replace("doctor_", "")
        ?.replace("patient_user_", "")
        ?.replace("patient_", "")
        ?.toIntOrNull()

    LaunchedEffect(roomId, testUploadId) {
        viewModel.loadMessages(roomId, testUploadId)
    }

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            delay(100) // Small delay to ensure UI is updated
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(otherUserName)
                        if (!currentTestUploadId.isNullOrEmpty()) {
                            Text(
                                "Test Report Available",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                actions = {
                    if (!currentTestUploadId.isNullOrEmpty() && userType == "doctor") {
                        IconButton(onClick = { showReportViewer = true }) {
                            Icon(
                                Icons.Default.Info,
                                "View Test Report",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Messages List
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (isLoading && messages.isEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (messages.isEmpty()) {
                    Text(
                        text = "No messages yet. Start the conversation!",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFECE5DD)), // WhatsApp background color
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(messages) { message ->
                            // Check if message is from current user by comparing both ID and type
                            val isFromCurrentUser = (message.senderId == currentUserNumericId && message.senderType == userType)
                            ChatMessageBubble(
                                message = message,
                                isCurrentUser = isFromCurrentUser
                            )
                        }
                    }
                }
            }

            // Message Input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message...") },
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(8.dp))

                FloatingActionButton(
                    onClick = {
                        val currentMessage = messageText
                        if (currentMessage.isNotBlank()) {
                            messageText = "" // Clear immediately before sending
                            viewModel.sendMessage(roomId, currentMessage.trim()) { success, error ->
                                if (!success) {
                                    // Restore message if sending failed
                                    messageText = currentMessage
                                }
                            }
                        }
                    },
                    modifier = Modifier.size(48.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        }
    }

    // Blood Test Report Viewer Dialog
    if (showReportViewer && !currentTestUploadId.isNullOrEmpty()) {
        BloodTestReportViewer(
            testUploadId = currentTestUploadId!!,
            onDismiss = { showReportViewer = false }
        )
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessageData,
    isCurrentUser: Boolean
) {
    val dateFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val timeString = try {
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(message.createdAt.substring(0, 19))
        dateFormatter.format(date ?: Date())
    } catch (e: Exception) {
        message.createdAt.substring(11, 16)
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isCurrentUser) 16.dp else 4.dp,
                bottomEnd = if (isCurrentUser) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isCurrentUser)
                    Color(0xFF128C7E) // WhatsApp green for sent messages
                else
                    Color(0xFFECE5DD) // Light beige for received messages
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = message.message,
                    color = if (isCurrentUser) Color.White else Color(0xFF303030),
                    fontSize = 15.sp,
                    lineHeight = 20.sp
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = timeString,
                    color = if (isCurrentUser) 
                        Color.White.copy(alpha = 0.7f) 
                    else 
                        Color(0xFF808080),
                    fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodTestReportViewer(
    testUploadId: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var showDiagnoseAI by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Blood Test Report")
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Test Type Info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Test Type: CBC (Complete Blood Count)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Upload ID: #$testUploadId",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                // Patient Notes Section
                Text(
                    "Patient Notes:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        "Review my blood test results. Any concerns?",
                        modifier = Modifier.padding(12.dp),
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Diagnose AI Button (clickable but immutable)
                Button(
                    onClick = { showDiagnoseAI = !showDiagnoseAI },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF9C27B0)
                    )
                ) {
                    Icon(Icons.Default.Star, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Diagnose AI")
                }

                if (showDiagnoseAI) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFF3E0)
                        )
                    ) {
                        Text(
                            "🤖 AI Diagnosis feature coming soon!",
                            fontSize = 12.sp,
                            color = Color(0xFFE65100),
                            modifier = Modifier.padding(12.dp),
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Open the file (you'll need to implement file download/viewing)
                    // For now, just show a message
                    val fileUrl = "http://10.0.2.2:8080/api/uploads/file/$testUploadId"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl))
                    context.startActivity(intent)
                }
            ) {
                Text("Open Report")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
