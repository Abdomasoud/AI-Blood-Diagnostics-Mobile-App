package com.aiblooddiagnostics.utils

import org.mindrot.jbcrypt.BCrypt

fun main() {
    val doctorPassword = "doctor123"
    val patientPassword = "patient123"
    
    val doctorHash = BCrypt.hashpw(doctorPassword, BCrypt.gensalt())
    val patientHash = BCrypt.hashpw(patientPassword, BCrypt.gensalt())
    
    println("Doctor password (doctor123) hash: $doctorHash")
    println("Patient password (patient123) hash: $patientHash")
    
    // Verify
    println("\nVerification:")
    println("Doctor password check: ${BCrypt.checkpw(doctorPassword, doctorHash)}")
    println("Patient password check: ${BCrypt.checkpw(patientPassword, patientHash)}")
}
