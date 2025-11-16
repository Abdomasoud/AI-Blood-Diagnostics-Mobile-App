package com.aiblooddiagnostics.routes

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.sql.ResultSet

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class SignupRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val userType: String, // "doctor" or "patient"
    val mobileNumber: String? = null,
    val specialization: String? = null,
    val experienceYears: Int? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val bloodType: String? = null
)

@Serializable
data class AuthResponse(
    val success: Boolean,
    val message: String,
    val userId: String? = null,
    val userType: String? = null,
    val fullName: String? = null,
    val email: String? = null
)

fun Route.authRoutes() {
    route("/api/auth") {
        post("/login") {
            val request = call.receive<LoginRequest>()
            
            val response = transaction {
                var result: AuthResponse? = null
                
                try {
                    // Check doctors table
                    val doctorQuery = "SELECT user_id, full_name, email, password_hash FROM doctors WHERE email = '${request.email}'"
                    exec(doctorQuery) { rs: ResultSet ->
                        if (rs.next()) {
                            val storedHash = rs.getString("password_hash")
                            application.log.info("Doctor found: ${request.email}, checking password...")
                            try {
                                if (BCrypt.checkpw(request.password, storedHash)) {
                                    result = AuthResponse(
                                        success = true,
                                        message = "Login successful",
                                        userId = rs.getString("user_id"),
                                        userType = "doctor",
                                        fullName = rs.getString("full_name"),
                                        email = rs.getString("email")
                                    )
                                    application.log.info("Doctor login successful")
                                } else {
                                    application.log.info("Password mismatch for doctor")
                                }
                            } catch (e: Exception) {
                                application.log.error("BCrypt error: ${e.message}")
                            }
                        } else {
                            application.log.info("Doctor not found: ${request.email}")
                        }
                    }
                    
                    if (result != null) return@transaction result!!
                    
                    // Check patients table
                    val patientQuery = "SELECT user_id, full_name, email, password_hash FROM patients WHERE email = '${request.email}'"
                    exec(patientQuery) { rs: ResultSet ->
                        if (rs.next()) {
                            val storedHash = rs.getString("password_hash")
                            application.log.info("Patient found: ${request.email}, checking password...")
                            try {
                                if (BCrypt.checkpw(request.password, storedHash)) {
                                    result = AuthResponse(
                                        success = true,
                                        message = "Login successful",
                                        userId = rs.getString("user_id"),
                                        userType = "patient",
                                        fullName = rs.getString("full_name"),
                                        email = rs.getString("email")
                                    )
                                    application.log.info("Patient login successful")
                                } else {
                                    application.log.info("Password mismatch for patient")
                                }
                            } catch (e: Exception) {
                                application.log.error("BCrypt error: ${e.message}")
                            }
                        } else {
                            application.log.info("Patient not found: ${request.email}")
                        }
                    }
                } catch (e: Exception) {
                    application.log.error("Login error: ${e.message}", e)
                }
                
                result ?: AuthResponse(success = false, message = "Invalid email or password")
            }
            
            call.respond(if (response.success) HttpStatusCode.OK else HttpStatusCode.Unauthorized, response)
        }
        
        post("/signup") {
            val request = call.receive<SignupRequest>()
            
            val response = transaction {
                try {
                    // Hash the password using BCrypt
                    val hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())
                    
                    if (request.userType == "doctor") {
                        val userId = "doctor_${System.currentTimeMillis()}"
                        val query = """
                            INSERT INTO doctors (user_id, full_name, email, password_hash, mobile_number, specialization, experience_years)
                            VALUES ('$userId', '${request.fullName}', '${request.email}', '$hashedPassword', 
                                    ${request.mobileNumber?.let { "'$it'" } ?: "NULL"}, 
                                    ${request.specialization?.let { "'$it'" } ?: "'Hematologist'"}, 
                                    ${request.experienceYears ?: 0})
                        """
                        exec(query)
                    } else {
                        val userId = "patient_${System.currentTimeMillis()}"
                        val query = """
                            INSERT INTO patients (user_id, full_name, email, password_hash, mobile_number, date_of_birth, gender, blood_type)
                            VALUES ('$userId', '${request.fullName}', '${request.email}', '$hashedPassword', 
                                    ${request.mobileNumber?.let { "'$it'" } ?: "NULL"},
                                    ${request.dateOfBirth?.let { "'$it'::date" } ?: "NULL"},
                                    ${request.gender?.let { "'$it'" } ?: "NULL"},
                                    ${request.bloodType?.let { "'$it'" } ?: "NULL"})
                        """
                        exec(query)
                    }
                    AuthResponse(success = true, message = "Signup successful")
                } catch (e: Exception) {
                    AuthResponse(success = false, message = "Signup failed: ${e.message}")
                }
            }
            
            call.respond(if (response.success) HttpStatusCode.Created else HttpStatusCode.BadRequest, response)
        }
    }
}
