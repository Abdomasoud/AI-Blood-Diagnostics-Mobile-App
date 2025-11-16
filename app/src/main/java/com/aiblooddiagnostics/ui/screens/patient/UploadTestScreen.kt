package com.aiblooddiagnostics.ui.screens.patient

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.aiblooddiagnostics.ui.viewmodel.PatientViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadTestScreen(
    navController: NavController,
    viewModel: PatientViewModel = hiltViewModel()
) {
    var selectedTestType by remember { mutableStateOf("CBC") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf<String>("") }
    var notes by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
        fileName = uri?.lastPathSegment ?: "Unknown file"
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upload Test Report") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Blue60,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Upload Your Lab Test Results",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Upload your blood test results to get AI-powered analysis and connect with doctors",
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Test Type Selection
            Text(
                text = "Test Type",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TestTypeChip(
                    text = "CBC",
                    selected = selectedTestType == "CBC",
                    onClick = { selectedTestType = "CBC" }
                )
                TestTypeChip(
                    text = "MSI",
                    selected = selectedTestType == "MSI",
                    onClick = { selectedTestType = "MSI" }
                )
                TestTypeChip(
                    text = "Both",
                    selected = selectedTestType == "Both",
                    onClick = { selectedTestType = "Both" }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // File Upload
            Text(
                text = "Upload File",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF5F5F5)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (selectedFileUri == null) {
                        Icon(
                            Icons.Default.CloudUpload,
                            contentDescription = "Upload",
                            modifier = Modifier.size(64.dp),
                            tint = Blue60
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Tap to select file",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { filePicker.launch("*/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = Blue60)
                        ) {
                            Text("Choose File")
                        }
                    } else {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "File Selected",
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFF4CAF50)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = fileName,
                            fontSize = 14.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { filePicker.launch("*/*") }) {
                            Text("Change File")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Notes
            Text(
                text = "Additional Notes (Optional)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("Add any additional information about your test...") },
                maxLines = 5,
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Error Message
            errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = "Error",
                            tint = Color(0xFFD32F2F)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = error,
                            color = Color(0xFFD32F2F),
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Upload Button
            Button(
                onClick = {
                    if (selectedFileUri != null) {
                        isUploading = true
                        errorMessage = null
                        viewModel.uploadTestFile(
                            fileUri = selectedFileUri!!,
                            testType = selectedTestType,
                            notes = notes.ifEmpty { null }
                        ) { success, message ->
                            isUploading = false
                            if (success) {
                                showSuccessDialog = true
                            } else {
                                errorMessage = message ?: "Upload failed"
                            }
                        }
                    } else {
                        errorMessage = "Please select a file to upload"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isUploading && selectedFileUri != null,
                colors = ButtonDefaults.buttonColors(containerColor = Blue60),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isUploading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Uploading...", fontSize = 16.sp)
                } else {
                    Icon(Icons.Default.CloudUpload, contentDescription = "Upload")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upload & Continue", fontSize = 16.sp)
                }
            }
        }
    }
    
    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Upload Successful!") },
            text = { Text("Your test results have been uploaded. Now select a doctor to review your results.") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        navController.navigate("select_doctor")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Blue60)
                ) {
                    Text("Select Doctor")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestTypeChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = { Text(text) },
        selected = selected,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Blue60,
            selectedLabelColor = Color.White,
            containerColor = Color(0xFFF5F5F5),
            labelColor = Color.Gray
        )
    )
}
