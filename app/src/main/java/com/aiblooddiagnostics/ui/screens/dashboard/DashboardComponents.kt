package com.aiblooddiagnostics.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aiblooddiagnostics.data.model.Diagnosis
import com.aiblooddiagnostics.data.model.Patient
import com.aiblooddiagnostics.ui.theme.Blue60

@Composable
fun DashboardButton(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Blue60
        ),
        border = BorderStroke(2.dp, Blue60),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun PatientsContent(
    navController: NavController,
    patients: List<Patient>,
    onDeletePatient: (Patient) -> Unit
) {
    if (patients.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "No patients",
                    modifier = Modifier.size(64.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No patients yet",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
                Text(
                    text = "Add a new patient to get started",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(patients) { patient ->
                PatientCard(
                    patient = patient,
                    onDeleteClick = { onDeletePatient(patient) }
                )
            }
        }
    }
}

@Composable
fun PatientCard(
    patient: Patient,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = patient.fullName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Age: ${patient.age} | Gender: ${patient.gender}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                patient.email?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete patient",
                    tint = Color.Red
                )
            }
        }
    }
}

@Composable
fun HistoryContent(diagnoses: List<Diagnosis>) {
    if (diagnoses.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.History,
                    contentDescription = "No history",
                    modifier = Modifier.size(64.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No diagnosis history",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
                Text(
                    text = "Completed diagnoses will appear here",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(diagnoses) { diagnosis ->
                DiagnosisCard(diagnosis = diagnosis)
            }
        }
    }
}

@Composable
fun DiagnosisCard(diagnosis: Diagnosis) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Patient ID: ${diagnosis.patientId.take(8)}...",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = diagnosis.status.capitalize(),
                    fontSize = 12.sp,
                    color = if (diagnosis.status == "completed") Color.Green else Color(0xFFFFA500),
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Test Type: ${diagnosis.testType}",
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            diagnosis.diagnosisResult?.let { result ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Result: $result",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
            
            diagnosis.recommendations?.let { recommendations ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Recommendations: $recommendations",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun BottomNavigation(navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Blue60),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { navController.navigate("dashboard") }) {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Home",
                    tint = Color.White
                )
            }
            IconButton(onClick = { 
                // Navigate to chat - for now using doctor_3 as default
                navController.navigate("chat_list/doctor_3/doctor")
            }) {
                Icon(
                    Icons.Default.Chat,
                    contentDescription = "Chat",
                    tint = Color.White.copy(alpha = 0.9f)
                )
            }
            IconButton(onClick = { navController.navigate("dashboard") }) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Color.White.copy(alpha = 0.9f)
                )
            }
            IconButton(onClick = { navController.navigate("appointments") }) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = "Calendar",
                    tint = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}