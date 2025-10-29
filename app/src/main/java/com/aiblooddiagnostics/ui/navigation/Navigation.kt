package com.aiblooddiagnostics.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aiblooddiagnostics.ui.screens.auth.LoginScreen
import com.aiblooddiagnostics.ui.viewmodel.AuthViewModel
import com.aiblooddiagnostics.ui.screens.auth.SignUpScreen
import com.aiblooddiagnostics.ui.screens.home.HomeScreen
import com.aiblooddiagnostics.ui.screens.dashboard.DashboardScreen
import com.aiblooddiagnostics.ui.screens.dashboard.DoctorDashboardScreen
import com.aiblooddiagnostics.ui.screens.dashboard.PatientDashboardScreen
import com.aiblooddiagnostics.ui.screens.diagnosis.DiagnosisScreen
import com.aiblooddiagnostics.ui.screens.patient.AddPatientScreen
import com.aiblooddiagnostics.ui.screens.patient.PatientDetailScreen
import com.aiblooddiagnostics.ui.screens.request.NewRequestScreen
import com.aiblooddiagnostics.ui.screens.doctors.DoctorsScreen
import com.aiblooddiagnostics.ui.screens.doctors.DoctorInfoScreen
import com.aiblooddiagnostics.ui.screens.appointments.AppointmentsScreen
import com.aiblooddiagnostics.ui.screens.payment.PaymentMethodScreen
import com.aiblooddiagnostics.ui.screens.payment.AddCardScreen
import com.aiblooddiagnostics.ui.screens.payment.PaymentScreen
import com.aiblooddiagnostics.ui.screens.success.SuccessScreen
import com.aiblooddiagnostics.ui.screens.chat.ChatListScreen
import com.aiblooddiagnostics.ui.screens.chat.ChatDetailScreen

@Composable
fun BloodDiagnosticsNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val startDestination = authViewModel.getStartDestination()
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("home") {
            HomeScreen(navController = navController)
        }
        
        composable("login") {
            LoginScreen(navController = navController)
        }
        
        composable("signup") {
            SignUpScreen(navController = navController)
        }
        
        composable("dashboard") {
            DashboardScreen(navController = navController)
        }
        
        composable("doctor_dashboard") {
            DoctorDashboardScreen(navController = navController)
        }
        
        composable("patient_dashboard") {
            PatientDashboardScreen(navController = navController)
        }
        
        composable("diagnosis") {
            DiagnosisScreen(navController = navController)
        }
        
        composable("add_patient") {
            AddPatientScreen(navController = navController)
        }
        
        composable("patient_detail/{patientId}") { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
            PatientDetailScreen(
                navController = navController,
                patientId = patientId
            )
        }
        
        composable("new_request") {
            NewRequestScreen(navController = navController)
        }
        
        composable("doctors") {
            DoctorsScreen(navController = navController)
        }
        
        composable("doctor_info/{doctorId}") { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
            DoctorInfoScreen(
                navController = navController,
                doctorId = doctorId
            )
        }
        
        composable("appointments") {
            AppointmentsScreen(navController = navController)
        }
        
        composable("payment_method") {
            PaymentMethodScreen(navController = navController)
        }
        
        composable("add_card") {
            AddCardScreen(navController = navController)
        }
        
        composable("payment/{appointmentId}") { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getString("appointmentId") ?: ""
            PaymentScreen(
                navController = navController,
                appointmentId = appointmentId
            )
        }
        
        composable("success/{type}") { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "diagnosis"
            SuccessScreen(
                navController = navController,
                successType = type
            )
        }
        
        composable("chat_list/{userId}/{userType}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val userType = backStackEntry.arguments?.getString("userType") ?: "patient"
            ChatListScreen(
                navController = navController,
                currentUserId = userId,
                currentUserType = userType
            )
        }
        
        composable("chat_detail/{chatRoomId}/{userId}/{userType}") { backStackEntry ->
            val chatRoomId = backStackEntry.arguments?.getString("chatRoomId") ?: ""
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val userType = backStackEntry.arguments?.getString("userType") ?: "patient"
            ChatDetailScreen(
                navController = navController,
                chatRoomId = chatRoomId,
                currentUserId = userId,
                currentUserType = userType
            )
        }
    }
}