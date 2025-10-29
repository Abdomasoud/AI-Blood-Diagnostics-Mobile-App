package com.aiblooddiagnostics.ui.screens.patient

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.aiblooddiagnostics.data.model.Patient
import com.aiblooddiagnostics.data.model.Diagnosis
import com.aiblooddiagnostics.ui.theme.Blue60
import com.aiblooddiagnostics.ui.viewmodel.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDetailScreen(
    navController: NavController,
    patientId: String,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val patients by viewModel.patients.collectAsState()
    val diagnoses by viewModel.diagnoses.collectAsState()
    
    val patient = patients.find { it.id == patientId }
    val patientDiagnoses = diagnoses.filter { it.patientId == patientId }
    
    var selectedTab by remember { mutableStateOf(0) }
    var medicalHistory by remember { mutableStateOf(patient?.medicalHistory ?: "") }
    var isEditing by remember { mutableStateOf(false) }

    if (patient == null) {
        // Show loading or error
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Patient not found")
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Blue60
                        )
                    }
                    Text(
                        text = "Back to Dashboard",
                        fontSize = 16.sp,
                        color = Blue60,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = { /* Unfollow patient */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE53E3E),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Unfollow", fontSize = 12.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Patient Info Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Patient",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Patient's Personal Profile",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Patient Details
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Name: ${patient.fullName}",
                            fontSize = 14.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Email: ${patient.email ?: "N/A"}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Age: ${patient.age}",
                            fontSize = 14.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Blood Type: ${patient.bloodType ?: "N/A"}",
                            fontSize = 14.sp,
                            color = Blue60,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Tab Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Medical History", "Reports and Tests", "Notes and Follow-up", "Upload New Test").forEachIndexed { index, title ->
                FilterChip(
                    onClick = { selectedTab = index },
                    label = { 
                        Text(
                            title, 
                            fontSize = 12.sp,
                            maxLines = 1
                        ) 
                    },
                    selected = selectedTab == index,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Blue60,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Tab Content
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> MedicalHistoryTab(
                    medicalHistory = medicalHistory,
                    onMedicalHistoryChange = { medicalHistory = it },
                    isEditing = isEditing,
                    onEditToggle = { isEditing = !isEditing },
                    onSave = {
                        // Save medical history
                        isEditing = false
                    }
                )
                1 -> ReportsAndTestsTab(diagnoses = patientDiagnoses)
                2 -> NotesAndFollowUpTab(
                    navController = navController,
                    patientId = patientId
                ) 
                3 -> UploadNewTestTab()
            }
        }
    }
}

@Composable
fun MedicalHistoryTab(
    medicalHistory: String,
    onMedicalHistoryChange: (String) -> Unit,
    isEditing: Boolean,
    onEditToggle: () -> Unit,
    onSave: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.MedicalServices,
                        contentDescription = "Medical History",
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "General Medical History",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (isEditing) {
                OutlinedTextField(
                    value = medicalHistory,
                    onValueChange = onMedicalHistoryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Blue60
                    ),
                    maxLines = 5
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onEditToggle) {
                        Text("Cancel", color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onSave,
                        colors = ButtonDefaults.buttonColors(containerColor = Blue60)
                    ) {
                        Text("Save Changes")
                    }
                }
            } else {
                Text(
                    text = medicalHistory.ifEmpty { "No medical history available" },
                    fontSize = 14.sp,
                    color = if (medicalHistory.isEmpty()) Color.Gray else Color.Black,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "You can only edit this field for patients you are following",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onEditToggle,
                    colors = ButtonDefaults.buttonColors(containerColor = Blue60),  
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Save Changes")
                }
            }
        }
    }
}

@Composable
fun ReportsAndTestsTab(diagnoses: List<Diagnosis>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(diagnoses) { diagnosis ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = diagnosis.testType,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(diagnosis.createdAt)),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = diagnosis.diagnosisResult ?: "Results pending...",
                        fontSize = 14.sp,
                        color = if (diagnosis.diagnosisResult != null) Color.Black else Color.Gray
                    )
                    
                    if (diagnosis.recommendations != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Recommendations: ${diagnosis.recommendations}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
        
        if (diagnoses.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No reports or tests available",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun NotesAndFollowUpTab(
    navController: NavController,
    patientId: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Chat,
            contentDescription = "Chat",
            modifier = Modifier.size(64.dp),
            tint = Blue60
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Notes and Follow-up",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Use the chat feature to communicate with the patient",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                navController.navigate("chat_detail/doctor_3_$patientId/doctor_3/doctor")
            },
            colors = ButtonDefaults.buttonColors(containerColor = Blue60)
        ) {
            Icon(Icons.Default.Chat, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Open Chat")
        }
    }
}

@Composable
fun UploadNewTestTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.CloudUpload,
            contentDescription = "Upload",
            modifier = Modifier.size(64.dp),
            tint = Blue60
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Upload New Test",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Upload new test results, documents, or images for this patient",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { /* Handle upload */ },
            colors = ButtonDefaults.buttonColors(containerColor = Blue60)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Choose File")
        }
    }
}