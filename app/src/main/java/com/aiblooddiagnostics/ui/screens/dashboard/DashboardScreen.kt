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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.aiblooddiagnostics.ui.theme.Blue60
import com.aiblooddiagnostics.ui.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val patients by viewModel.patients.collectAsState()
    val diagnoses by viewModel.diagnoses.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Header
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
                                text = "Hi, WelcomeBack",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "Dr Amira Mohamed",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                    IconButton(onClick = { 
                        // Logout - go back to home screen
                        navController.navigate("home") {
                            popUpTo("dashboard") { inclusive = true }
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
                    text = "Ready to assist you with AI- powered blood diagnosis.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Select a patient or begin a new assessment.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }

        // Tab Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Patients", "History").forEachIndexed { index, title ->
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

        // Main Content
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> PatientsContent(
                    navController = navController,
                    patients = patients,
                    onDeletePatient = { viewModel.deletePatient(it) }
                )
                1 -> HistoryContent(
                    diagnoses = diagnoses
                )
            }
        }

        // Dashboard Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dashboard",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .background(
                                Blue60,
                                RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DashboardButton(
                        title = "Add Patient",
                        icon = Icons.Default.PersonAdd,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate("diagnosis") }
                    )

                    DashboardButton(
                        title = "Run AI Diagnosis",
                        icon = Icons.Default.Psychology,
                        modifier = Modifier.weight(1f),
                        onClick = { 
                            // Mock AI diagnosis
                            viewModel.runMockAIDiagnosis()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                DashboardButton(
                    title = "View History",
                    icon = Icons.Default.History,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { selectedTab = 1 }
                )
            }
        }

        // Bottom Navigation
        BottomNavigation(navController = navController)
    }
}