package com.aiblooddiagnostics.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val fullName: String,
    val email: String,
    val password: String,
    val mobileNumber: String? = null,
    val dateOfBirth: String? = null,
    val userType: String = "patient", // patient, doctor
    val createdAt: Long = System.currentTimeMillis()
)