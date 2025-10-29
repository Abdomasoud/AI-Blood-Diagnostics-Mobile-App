package com.aiblooddiagnostics.ui.screens.request

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.aiblooddiagnostics.ui.theme.Blue60
import com.aiblooddiagnostics.ui.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRequestScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    var selectedTestType by remember { mutableStateOf("CBC") }
    var notes by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var showFileOptions by remember { mutableStateOf(false) }
    var selectedFileType by remember { mutableStateOf("Image") }
    
    val testTypes = listOf("CBC", "MSI", "Both")
    val fileTypes = listOf("Image", "Document")
    val mockFiles = listOf(
        "blood_test_sample.jpg",
        "lab_results_2024.pdf",
        "doctor_prescription.jpg",
        "previous_cbc_report.pdf",
        "blood_sample_image.jpg"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "New Test Request", 
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Blue60)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Request Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Test Request Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    // Test Type Selection
                    Text(
                        text = "Test Type",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(testTypes) { testType ->
                            FilterChip(
                                onClick = { selectedTestType = testType },
                                label = { Text(testType) },
                                selected = selectedTestType == testType,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Blue60,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                    
                    // Notes Section
                    Text(
                        text = "Additional Notes (Optional)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                    
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Any symptoms or concerns...") },
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
            
            // File Upload Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = "Upload",
                        tint = Blue60,
                        modifier = Modifier.size(48.dp)
                    )
                    
                    Text(
                        text = "Upload Test Documents",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    
                    // File Type Selection
                    Text(
                        text = "File Type",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(fileTypes) { fileType ->
                            FilterChip(
                                onClick = { selectedFileType = fileType },
                                label = { Text(fileType) },
                                selected = selectedFileType == fileType,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Blue60,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                    
                    Text(
                        text = "Upload previous test results or doctor's prescription",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    
                    OutlinedButton(
                        onClick = { showFileOptions = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(selectedFileName ?: "Choose File")
                    }
                    
                    if (selectedFileName != null) {
                        Text(
                            text = "Selected: $selectedFileName",
                            fontSize = 12.sp,
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
            
            // Submit Button
            Button(
                onClick = {
                    isLoading = true
                    // Create a new diagnosis request
                    viewModel.createDiagnosisRequest(
                        testType = selectedTestType,
                        fileType = selectedFileType,
                        filePath = selectedFileName,
                        notes = notes
                    )
                    navController.navigate("success/request") {
                        popUpTo("patient_dashboard") { inclusive = false }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Blue60),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "Submit Request",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Info Text
            Text(
                text = "Your request will be reviewed by our medical team and you'll receive results within 24-48 hours.",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
    
    // File Selection Dialog
    if (showFileOptions) {
        AlertDialog(
            onDismissRequest = { showFileOptions = false },
            title = { Text("Select Mock File") },
            text = {
                LazyColumn {
                    items(mockFiles) { fileName ->
                        TextButton(
                            onClick = {
                                selectedFileName = fileName
                                showFileOptions = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Icon(
                                    imageVector = if (fileName.endsWith(".pdf")) Icons.Default.Description else Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(fileName)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFileOptions = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}