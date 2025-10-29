package com.aiblooddiagnostics.ui.screens.diagnosis

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import com.aiblooddiagnostics.ui.viewmodel.DiagnosisViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosisScreen(
    navController: NavController,
    viewModel: DiagnosisViewModel = hiltViewModel()
) {
    val doctors by viewModel.doctors.collectAsState()
    val saveState by viewModel.saveState.collectAsState()
    
    var selectedDoctor by remember { mutableStateOf<String?>(null) }
    var patientName by remember { mutableStateOf("") }
    var patientAge by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("") }
    var selectedTestType by remember { mutableStateOf("") }
    var selectedFileType by remember { mutableStateOf("") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
    }

    val currentDate = remember {
        SimpleDateFormat("Month dd, yyyy", Locale.getDefault()).format(Date())
    }
    
    val currentTime = remember {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(rememberScrollState())
    ) {
        // Header
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Blue60
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Diagnosis",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Doctor Selection Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = "https://via.placeholder.com/50",
                        contentDescription = "Selected Doctor",
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        val doctor = doctors.find { it.id == selectedDoctor }
                        Text(
                            text = doctor?.name ?: "Select Doctor",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = doctor?.specialization ?: "Hematologist",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                if (doctors.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(doctors) { doctor ->
                            DoctorSelectionCard(
                                doctor = doctor,
                                isSelected = selectedDoctor == doctor.id,
                                onSelect = { selectedDoctor = doctor.id }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Date and Time Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Date",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = currentDate,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
                Column {
                    Text(
                        text = "Time",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = currentTime,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Patient Details Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Patient Details",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Full Name",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = patientName,
                    onValueChange = { patientName = it },
                    placeholder = { Text("Jane Doe", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Blue60,
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Age",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = patientAge,
                    onValueChange = { patientAge = it },
                    placeholder = { Text("30", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Blue60,
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Gender",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    listOf("Male", "Female").forEach { gender ->
                        FilterChip(
                            onClick = { selectedGender = gender },
                            label = { Text(gender) },
                            selected = selectedGender == gender,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Blue60,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Test Selection Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Start attaching the test you want to diagnose.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Choose The Type Of The Test.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    listOf("CBC", "MSI", "Both").forEach { testType ->
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

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Choose The Type Of The File.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FileTypeButton(
                        title = "Document",
                        icon = Icons.Default.Description,
                        isSelected = selectedFileType == "Document",
                        onClick = { 
                            selectedFileType = "Document"
                            filePickerLauncher.launch("application/*")
                        }
                    )
                    FileTypeButton(
                        title = "Image",
                        icon = Icons.Default.Image,
                        isSelected = selectedFileType == "Image",
                        onClick = { 
                            selectedFileType = "Image"
                            filePickerLauncher.launch("image/*")
                        }
                    )
                }

                selectedFileUri?.let { uri ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "File selected: ${uri.lastPathSegment}",
                        fontSize = 12.sp,
                        color = Color.Green
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Run Diagnosis Button
        Button(
            onClick = {
                if (selectedDoctor != null && patientName.isNotBlank() && 
                    patientAge.isNotBlank() && selectedGender.isNotBlank() && 
                    selectedTestType.isNotBlank() && selectedFileType.isNotBlank()) {
                    
                    viewModel.saveDiagnosis(
                        doctorId = selectedDoctor!!,
                        patientName = patientName,
                        patientAge = patientAge.toIntOrNull() ?: 0,
                        gender = selectedGender,
                        testType = selectedTestType,
                        fileType = selectedFileType,
                        fileUri = selectedFileUri?.toString()
                    ) { success ->
                        if (success) {
                            navController.navigate("success/diagnosis")
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Blue60),
            shape = RoundedCornerShape(28.dp),
            enabled = selectedDoctor != null && patientName.isNotBlank() && 
                     patientAge.isNotBlank() && selectedGender.isNotBlank() && 
                     selectedTestType.isNotBlank() && selectedFileType.isNotBlank()
        ) {
            if (saveState.isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = "Run Diagnosis",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        saveState.error?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}