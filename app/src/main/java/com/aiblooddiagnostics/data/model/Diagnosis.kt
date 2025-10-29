package com.aiblooddiagnostics.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "diagnoses")
data class Diagnosis(
    @PrimaryKey val id: String,
    val patientId: String,
    val doctorId: String,
    val testType: String, // CBC, MSI, Both
    val fileType: String, // Document, Image
    val filePath: String? = null,
    val diagnosisResult: String? = null,
    val recommendations: String? = null,
    val status: String = "pending", // pending, completed
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
) : Parcelable