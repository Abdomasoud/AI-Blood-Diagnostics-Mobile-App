package com.aiblooddiagnostics.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiblooddiagnostics.data.model.*
import com.aiblooddiagnostics.data.repository.BloodDiagnosticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: BloodDiagnosticsRepository
) : ViewModel() {

    // For now, we'll use a hardcoded doctor ID (Dr. Amira Mohamed)
    // In a real app, this would come from the logged-in user session
    private var currentDoctorId = "doctor_3"

    // Get only patients who chose THIS doctor
    val patients = repository.getPatientsByDoctor(currentDoctorId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Get only diagnoses for THIS doctor
    val diagnoses = repository.getDiagnosesByDoctor(currentDoctorId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        initializeMockData()
    }

    private fun initializeMockData() {
        viewModelScope.launch {
            try {
                // Force re-initialization of user data to fix login issues
                println("DEBUG: Initializing mock data...")
                
                // Always ensure test users exist with correct types
                
                // Initialize mock doctors
                val mockDoctors = listOf(
                Doctor(
                    id = "doctor_1",
                    name = "Dr. Moataz Bahaa, Ph.D.",
                    specialization = "Hematologist",
                    email = "moataz.bahaa@hospital.com",
                    yearsOfExperience = 15,
                    rating = 5.0f,
                    reviewCount = 40,
                    focus = "The impact of hormonal imbalances on skin conditions, specializing in acne, alopecia, eczema, and other skin disorders.",
                    careerPath = "Trained in internal medicine with a specialized fellowship in hematology. I have worked in both academic and clinical settings, managing complex cases and collaborating with multidisciplinary teams. My expertise includes all aspects of care, inpatient consultations, bone marrow procedures, and participation in clinical trials focused on novel therapeutics and precision medicine in hematology.",
                    highlights = "Skilled in treating both common and serious blood disorders Experienced in tests like bone marrow biopsies and flow cytometry Involved in research with articles published in medical journals Doctor-focused on helping patients understand their condition and treatment Helped train young doctors and worked on improving care in the department"
                ),
                Doctor(
                    id = "doctor_2",
                    name = "Dr. Mark Phelopateer, M.D.",
                    specialization = "Hematologist",
                    email = "mark.phelopateer@hospital.com",
                    yearsOfExperience = 12,
                    rating = 4.8f,
                    reviewCount = 35,
                    focus = "Specialized in blood disorders and cancer treatment with focus on innovative therapies.",
                    careerPath = "Board-certified hematologist with extensive experience in treating blood cancers and disorders.",
                    highlights = "Expert in bone marrow transplants and cellular therapy with multiple research publications."
                ),
                Doctor(
                    id = "doctor_3",
                    name = "Dr. Amira Mohamed, M.D.",
                    specialization = "Hematologist",
                    email = "amira.mohamed@hospital.com",
                    yearsOfExperience = 10,
                    rating = 4.9f,
                    reviewCount = 60,
                    focus = "Clinical hematology with emphasis on diagnostic accuracy and patient care.",
                    careerPath = "Experienced in both clinical practice and medical education in hematology.",
                    highlights = "Known for precise diagnosis and compassionate patient care approach."
                ),
                Doctor(
                    id = "doctor_4",
                    name = "Dr. Nabila Ahmed, Ph.D.",
                    specialization = "Hematologist",
                    email = "nabila.ahmed@hospital.com",
                    yearsOfExperience = 18,
                    rating = 4.7f,
                    reviewCount = 45,
                    focus = "Research-oriented hematologist with focus on rare blood disorders.",
                    careerPath = "Academic hematologist with dual focus on patient care and research.",
                    highlights = "Leading researcher in rare blood disorders with numerous published studies."
                )
            )
            
            repository.insertDoctors(mockDoctors)
            
            // Initialize test users for easy login
            val testUsers = listOf(
                // Doctor Account
                User(
                    id = "doctor_user_1",
                    fullName = "Dr. Amira Mohamed",
                    email = "doctor@hospital.com",
                    password = "doctor123",
                    userType = "doctor",
                    mobileNumber = "+201234567890",
                    dateOfBirth = "1985-05-15"
                ),
                // Patient Accounts - Multiple for testing
                User(
                    id = "patient_user_1",
                    fullName = "Patient Ahmed",
                    email = "patient@test.com",
                    password = "patient123",
                    userType = "patient",
                    mobileNumber = "+201098765432",
                    dateOfBirth = "1990-08-20"
                ),
                User(
                    id = "patient_user_2", 
                    fullName = "Sarah Mohamed",
                    email = "sarah@patient.com",
                    password = "patient123",
                    userType = "patient",
                    mobileNumber = "+201555666777",
                    dateOfBirth = "1988-03-12"
                ),
                User(
                    id = "patient_user_3",
                    fullName = "Ahmed Ali",
                    email = "ahmed@patient.com", 
                    password = "patient123",
                    userType = "patient",
                    mobileNumber = "+201777888999",
                    dateOfBirth = "1992-11-08"
                ),
                User(
                    id = "patient_user_4",
                    fullName = "Mona Hassan",
                    email = "mona@patient.com",
                    password = "patient123", 
                    userType = "patient",
                    mobileNumber = "+201444555666",
                    dateOfBirth = "1985-07-25"
                )
            )
            
            // Force insert/update test users to ensure correct userType
            testUsers.forEach { user ->
                println("DEBUG: Inserting user: ${user.email} with userType: ${user.userType}")
                repository.registerUser(user) // This will replace existing users
            }

            // Initialize mock patients linked to doctors
            val mockPatients = listOf(
                Patient(
                    id = "patient_1",
                    fullName = "Patient Ahmed",
                    age = 32,
                    gender = "Male",
                    email = "patient@test.com",
                    mobileNumber = "+201098765432",
                    bloodType = "B+",
                    medicalHistory = "12µg"
                ),
                Patient(
                    id = "patient_2",
                    fullName = "Sarah Mohamed",
                    age = 28,
                    gender = "Female",
                    email = "sarah@patient.com",
                    mobileNumber = "+201555666777",
                    bloodType = "A+",
                    medicalHistory = "No significant medical history"
                ),
                Patient(
                    id = "patient_3",
                    fullName = "Ahmed Ali",
                    age = 35,
                    gender = "Male",
                    email = "ahmed@patient.com",
                    mobileNumber = "+201777888999",
                    bloodType = "O+",
                    medicalHistory = "History of mild hypertension"
                )
            )

            mockPatients.forEach { patient ->
                val existingPatient = repository.getPatientById(patient.id)
                if (existingPatient == null) {
                    repository.insertPatient(patient)
                }
            }

            // Initialize mock diagnoses
            val mockDiagnoses = listOf(
                Diagnosis(
                    id = "diagnosis_1",
                    patientId = "patient_1",
                    doctorId = "doctor_3", // Dr. Amira Mohamed
                    testType = "CBC",
                    status = "completed",
                    diagnosisResult = "Mild anemia detected. Hemoglobin: 11.2 g/dL (Normal: 12-16 g/dL)",
                    recommendations = "Iron supplements recommended. Include iron-rich foods in diet. Follow-up in 3 months.",
                    createdAt = System.currentTimeMillis() - 86400000, // 1 day ago
                    fileType = "Image",
                    filePath = "blood_test_1.jpg"
                ),
                Diagnosis(
                    id = "diagnosis_2",
                    patientId = "patient_2",
                    doctorId = "doctor_3", // Dr. Amira Mohamed
                    testType = "MSI",
                    status = "completed",
                    diagnosisResult = "All values within normal range. Excellent health markers.",
                    recommendations = "Continue current lifestyle. Annual check-up recommended.",
                    createdAt = System.currentTimeMillis() - 172800000, // 2 days ago
                    fileType = "Document",
                    filePath = "lab_report_2.pdf"
                ),
                Diagnosis(
                    id = "diagnosis_3",
                    patientId = "patient_3",
                    doctorId = "doctor_3", // Dr. Amira Mohamed
                    testType = "Both",
                    status = "pending",
                    diagnosisResult = null,
                    recommendations = null,
                    createdAt = System.currentTimeMillis(),
                    fileType = "Image",
                    filePath = "blood_sample_3.jpg"
                )
            )

            mockDiagnoses.forEach { diagnosis ->
                val existingDiagnosis = repository.getDiagnosisById(diagnosis.id)
                if (existingDiagnosis == null) {
                    repository.insertDiagnosis(diagnosis)
                }
            }
            } catch (e: Exception) {
                // Handle initialization errors gracefully
                e.printStackTrace()
            }
        }
    }

    fun deletePatient(patient: Patient) {
        viewModelScope.launch {
            try {
                repository.deletePatient(patient)
                // Also delete related diagnoses
                repository.deleteDiagnosesByPatient(patient.id)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deletePatientById(patientId: String) {
        viewModelScope.launch {
            try {
                repository.deletePatientById(patientId)
                // Also delete related diagnoses
                repository.deleteDiagnosesByPatient(patientId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun addPatient(
        fullName: String,
        age: Int,
        gender: String,
        email: String? = null,
        mobileNumber: String? = null,
        bloodType: String? = null,
        medicalHistory: String? = null
    ) {
        viewModelScope.launch {
            try {
                val patient = Patient(
                    id = "PAT_${System.currentTimeMillis()}",
                    fullName = fullName,
                    age = age,
                    gender = gender,
                    email = email,
                    mobileNumber = mobileNumber,
                    bloodType = bloodType,
                    medicalHistory = medicalHistory,
                    createdAt = System.currentTimeMillis()
                )
                repository.insertPatient(patient)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun runMockAIDiagnosis() {
        viewModelScope.launch {
            // Get pending diagnoses for THIS doctor only
            val allPendingDiagnoses = repository.getDiagnosesByStatus("pending").first()
            val doctorPendingDiagnoses = allPendingDiagnoses.filter { it.doctorId == currentDoctorId }
            
            val diagnosisToUpdate = if (doctorPendingDiagnoses.isNotEmpty()) {
                doctorPendingDiagnoses.first()
            } else {
                // Create a mock diagnosis for demonstration
                val mockPatient = Patient(
                    id = "mock_patient_${System.currentTimeMillis()}",
                    fullName = "Jane Doe",
                    age = 30,
                    gender = "Female"
                )
                repository.insertPatient(mockPatient)
                
                Diagnosis(
                    id = "diagnosis_${System.currentTimeMillis()}",
                    patientId = mockPatient.id,
                    doctorId = currentDoctorId, // Current doctor
                    testType = "CBC",
                    fileType = "Document"
                )
            }

            // Mock AI diagnosis result
            val mockResults = listOf(
                "Blood analysis shows normal white blood cell count within healthy range (4,500-11,000 cells/μL). Red blood cell count and hemoglobin levels are optimal. No signs of anemia or infection detected.",
                "Complete Blood Count indicates mild iron deficiency. Recommend iron supplements and dietary modifications. Follow-up testing in 6-8 weeks advised.",
                "Blood work reveals elevated lymphocytes, suggesting possible viral infection. Recommend rest, hydration, and follow-up if symptoms persist beyond 7 days.",
                "All blood parameters within normal ranges. Patient shows excellent overall health markers. Continue current lifestyle and routine check-ups."
            )

            val mockRecommendations = listOf(
                "Maintain balanced diet rich in iron, vitamin B12, and folate. Continue regular exercise and adequate sleep.",
                "Start iron supplement 325mg daily with vitamin C. Avoid tea/coffee with meals. Include iron-rich foods like spinach, red meat, and beans.",
                "Get adequate rest and increase fluid intake. Monitor temperature and symptoms. Return if fever exceeds 101°F or symptoms worsen.",
                "Continue healthy lifestyle. Schedule annual blood work. Consider preventive screenings based on age and family history."
            )

            val randomResult = mockResults.random()
            val randomRecommendation = mockRecommendations.random()

            val updatedDiagnosis = diagnosisToUpdate.copy(
                diagnosisResult = randomResult,
                recommendations = randomRecommendation,
                status = "completed",
                completedAt = System.currentTimeMillis()
            )

            repository.updateDiagnosis(updatedDiagnosis)
        }
    }

    // Method to set the current doctor (for future login integration)
    fun setCurrentDoctor(doctorId: String) {
        currentDoctorId = doctorId
        // Refresh the data flows (this would typically trigger recomposition)
    }
    
    fun createDiagnosisRequest(
        testType: String,
        fileType: String,
        filePath: String? = null,
        notes: String = ""
    ) {
        viewModelScope.launch {
            try {
                val diagnosis = Diagnosis(
                    id = "DIAG_${System.currentTimeMillis()}",
                    patientId = "patient_1", // In real app, this would come from logged-in patient
                    doctorId = currentDoctorId,
                    testType = testType,
                    fileType = fileType,
                    filePath = filePath,
                    diagnosisResult = null, // Will be filled by doctor
                    recommendations = notes,
                    status = "pending",
                    createdAt = System.currentTimeMillis()
                )
                repository.insertDiagnosis(diagnosis)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}