package com.aiblooddiagnostics.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiblooddiagnostics.data.model.User
import com.aiblooddiagnostics.data.repository.BloodDiagnosticsRepository
import com.aiblooddiagnostics.data.manager.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val user: User? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: BloodDiagnosticsRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _loginState = MutableStateFlow(AuthState())
    val loginState: StateFlow<AuthState> = _loginState.asStateFlow()

    private val _signUpState = MutableStateFlow(AuthState())
    val signUpState: StateFlow<AuthState> = _signUpState.asStateFlow()

    fun login(email: String, password: String, onResult: (com.aiblooddiagnostics.data.model.User?) -> Unit) {
        viewModelScope.launch {
            _loginState.value = _loginState.value.copy(isLoading = true, error = null)
            
            try {
                val user = repository.loginUser(email, password)
                if (user != null) {
                    // Save session data
                    sessionManager.saveLoginSession(
                        userId = user.id,
                        userType = user.userType,
                        userName = user.fullName,
                        userEmail = user.email
                    )
                    
                    _loginState.value = _loginState.value.copy(
                        isLoading = false,
                        user = user,
                        error = null
                    )
                    onResult(user)
                } else {
                    _loginState.value = _loginState.value.copy(
                        isLoading = false,
                        error = "Invalid email or password"
                    )
                    onResult(null)
                }
            } catch (e: Exception) {
                _loginState.value = _loginState.value.copy(
                    isLoading = false,
                    error = "Login failed: ${e.message}"
                )
                onResult(null)
            }
        }
    }

    fun signUp(
        fullName: String,
        email: String,
        password: String,
        userType: String,
        mobileNumber: String? = null,
        dateOfBirth: String? = null,
        gender: String? = null,
        bloodType: String? = null,
        specialization: String? = null,
        experienceYears: Int? = null,
        onResult: (User?) -> Unit
    ) {
        viewModelScope.launch {
            _signUpState.value = _signUpState.value.copy(isLoading = true, error = null)
            
            try {
                val newUser = User(
                    id = UUID.randomUUID().toString(),
                    fullName = fullName,
                    email = email,
                    password = password,
                    userType = userType,
                    mobileNumber = mobileNumber,
                    dateOfBirth = dateOfBirth,
                    gender = gender,
                    bloodType = bloodType,
                    specialization = specialization,
                    experienceYears = experienceYears
                )

                val success = repository.registerUser(newUser)
                if (success) {
                    // Save session data
                    sessionManager.saveLoginSession(
                        userId = newUser.id,
                        userType = newUser.userType,
                        userName = newUser.fullName,
                        userEmail = newUser.email
                    )
                    
                    _signUpState.value = _signUpState.value.copy(
                        isLoading = false,
                        user = newUser,
                        error = null
                    )
                    onResult(newUser)
                } else {
                    _signUpState.value = _signUpState.value.copy(
                        isLoading = false,
                        error = "Sign up failed. Please try again."
                    )
                    onResult(null)
                }
            } catch (e: Exception) {
                _signUpState.value = _signUpState.value.copy(
                    isLoading = false,
                    error = "Sign up failed: ${e.message}"
                )
                onResult(null)
            }
        }
    }
    
    fun logout() {
        _loginState.value = AuthState()
        _signUpState.value = AuthState()
        sessionManager.clearSession()
    }
    
    fun isUserLoggedIn(): Boolean {
        return sessionManager.isLoggedIn()
    }
    
    fun getStartDestination(): String {
        return sessionManager.getStartDestination()
    }
    
    fun getUserName(): String? {
        return sessionManager.getUserName()
    }
    
    fun getUserId(): String? {
        return sessionManager.getUserId()
    }
    
    fun getUserEmail(): String? {
        return sessionManager.getUserEmail()
    }
}