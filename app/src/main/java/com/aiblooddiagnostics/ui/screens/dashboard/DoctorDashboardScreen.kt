package com.aiblooddiagnostics.ui.screens.dashboard

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.aiblooddiagnostics.ui.theme.Blue60
import com.aiblooddiagnostics.ui.viewmodel.DashboardViewModel
import com.aiblooddiagnostics.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val patients by viewModel.patients.collectAsState()
    val diagnoses by viewModel.diagnoses.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Doctor Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp)
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
                        AsyncImage(
                            model = "https://via.placeholder.com/50",
                            contentDescription = "Doctor Avatar",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Hi, Welcome Back Doctor",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "Dr. Amira Mohamed",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "Cardiologist • 5 years exp",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    IconButton(onClick = { 
                        authViewModel.logout()
                        navController.navigate("home") {
                            popUpTo("doctor_dashboard") { inclusive = true }
                        }
                    }) {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Manage your patients and provide AI-powered blood diagnosis.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Statistics Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatisticCard(
                title = "Total Patients",
                value = patients.size.toString(),
                color = Color(0xFF2196F3),
                icon = Icons.Default.People,
                modifier = Modifier.weight(1f)
            )
            StatisticCard(
                title = "Pending Tests", 
                value = diagnoses.count { it.status == "pending" }.toString(),
                color = Color(0xFFFF9800),
                icon = Icons.Default.Schedule,
                modifier = Modifier.weight(1f)
            )
            StatisticCard(
                title = "Completed Reports",
                value = diagnoses.count { it.status == "completed" }.toString(),
                color = Color(0xFF4CAF50),
                icon = Icons.Default.CheckCircle,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                title = "Add Patient",
                icon = Icons.Default.PersonAdd,
                color = Blue60,
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("add_patient") }
            )
            QuickActionCard(
                title = "AI Diagnosis",
                icon = Icons.Default.Psychology,
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f),
                onClick = { viewModel.runMockAIDiagnosis() }
            )
            QuickActionCard(
                title = "Chat",
                icon = Icons.Default.Chat,
                color = Color(0xFFFF9800),
                modifier = Modifier.weight(1f),
                onClick = { 
                    navController.navigate("chat_list/doctor_3/doctor")
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tab Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("My Patients", "History", "Reports").forEachIndexed { index, title ->
                FilterChip(
                    onClick = { selectedTab = index },
                    label = { Text(title) },
                    selected = selectedTab == index,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Blue60,
                        selectedLabelColor = Color.White
                    )
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { /* Search */ }) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Content based on selected tab
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> DoctorPatientsContent(
                    navController = navController,
                    patients = patients,
                    onDeletePatient = { patientId -> viewModel.deletePatientById(patientId) }
                )
                1 -> DoctorHistoryContent(
                    diagnoses = diagnoses
                )
                2 -> DoctorReportsContent()
            }
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun DoctorPatientsContent(
    navController: NavController,
    patients: List<com.aiblooddiagnostics.data.model.Patient>,
    onDeletePatient: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(patients) { patient ->
            PatientCard(
                patient = patient,
                onDeleteClick = { onDeletePatient(patient.id) },
                onChatClick = { 
                    navController.navigate("chat_detail/doctor_3_${patient.id}/doctor_3/doctor")
                },
                onViewClick = { 
                    navController.navigate("patient_detail/${patient.id}")
                }
            )
        }
    }
}

@Composable
fun PatientCard(
    patient: com.aiblooddiagnostics.data.model.Patient,
    onDeleteClick: () -> Unit,
    onChatClick: () -> Unit,
    onViewClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    AsyncImage(
                        model = "https://via.placeholder.com/40",
                        contentDescription = "Patient Avatar",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = patient.fullName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "Age: ${patient.age} • ${patient.gender}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        patient.mobileNumber?.let { phoneNumber ->
                            Text(
                                text = phoneNumber,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
                
                Row {
                    IconButton(onClick = onChatClick) {
                        Icon(
                            Icons.Default.Chat,
                            contentDescription = "Chat",
                            tint = Blue60,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onViewClick) {
                        Icon(
                            Icons.Default.Visibility,
                            contentDescription = "View",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DoctorHistoryContent(
    diagnoses: List<com.aiblooddiagnostics.data.model.Diagnosis>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(diagnoses) { diagnosis ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Patient ID: ${diagnosis.patientId}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = diagnosis.testType,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        Text(
                            text = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date(diagnosis.createdAt)),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = diagnosis.diagnosisResult ?: "No results yet",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun DoctorReportsContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Assessment,
            contentDescription = "Reports",
            modifier = Modifier.size(64.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Reports Coming Soon",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Text(
            text = "Analytics and reports will be available here",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun StatisticCard(
    title: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(80.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = value,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}