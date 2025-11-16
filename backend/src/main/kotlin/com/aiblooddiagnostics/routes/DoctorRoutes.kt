package com.aiblooddiagnostics.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.ResultSet

@Serializable
data class DoctorInfo(
    val id: Int,
    val userId: String,
    val fullName: String,
    val email: String,
    val specialization: String,
    val experienceYears: Int,
    val rating: Double,
    val bio: String?
)

@Serializable
data class DoctorListResponse(
    val success: Boolean,
    val doctors: List<DoctorInfo>,
    val message: String? = null
)

fun Route.doctorRoutes() {
    route("/api/doctors") {
        // Get all doctors
        get {
            val response = transaction {
                try {
                    val doctors = mutableListOf<DoctorInfo>()
                    val query = """
                        SELECT id, user_id, full_name, email, specialization, 
                               experience_years, rating, bio 
                        FROM doctors 
                        ORDER BY rating DESC
                    """
                    
                    exec(query) { rs: ResultSet ->
                        while (rs.next()) {
                            doctors.add(
                                DoctorInfo(
                                    id = rs.getInt("id"),
                                    userId = rs.getString("user_id"),
                                    fullName = rs.getString("full_name"),
                                    email = rs.getString("email"),
                                    specialization = rs.getString("specialization"),
                                    experienceYears = rs.getInt("experience_years"),
                                    rating = rs.getDouble("rating"),
                                    bio = rs.getString("bio")
                                )
                            )
                        }
                    }
                    
                    DoctorListResponse(success = true, doctors = doctors)
                } catch (e: Exception) {
                    application.log.error("Error fetching doctors: ${e.message}", e)
                    DoctorListResponse(success = false, doctors = emptyList(), message = e.message)
                }
            }
            
            call.respond(HttpStatusCode.OK, response)
        }
        
        // Get doctor by ID
        get("/{id}") {
            val doctorId = call.parameters["id"]
            
            val response = transaction {
                try {
                    var doctor: DoctorInfo? = null
                    val query = """
                        SELECT id, user_id, full_name, email, specialization, 
                               experience_years, rating, bio 
                        FROM doctors 
                        WHERE id = $doctorId OR user_id = '$doctorId'
                    """
                    
                    exec(query) { rs: ResultSet ->
                        if (rs.next()) {
                            doctor = DoctorInfo(
                                id = rs.getInt("id"),
                                userId = rs.getString("user_id"),
                                fullName = rs.getString("full_name"),
                                email = rs.getString("email"),
                                specialization = rs.getString("specialization"),
                                experienceYears = rs.getInt("experience_years"),
                                rating = rs.getDouble("rating"),
                                bio = rs.getString("bio")
                            )
                        }
                    }
                    
                    doctor
                } catch (e: Exception) {
                    application.log.error("Error fetching doctor: ${e.message}", e)
                    null
                }
            }
            
            if (response != null) {
                call.respond(HttpStatusCode.OK, response)
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Doctor not found"))
            }
        }
    }
}
