package com.aiblooddiagnostics.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "doctor_patient_connections")
data class DoctorPatientConnection(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val patientId: String,
    val doctorId: String,
    val status: String = "pending", // pending, approved, rejected
    val requestDate: Date = Date(),
    val approvalDate: Date? = null,
    val notes: String? = null
)
