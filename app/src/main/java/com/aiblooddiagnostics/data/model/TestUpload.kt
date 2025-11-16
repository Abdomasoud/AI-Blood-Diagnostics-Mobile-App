package com.aiblooddiagnostics.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "test_uploads")
data class TestUpload(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val patientId: String,
    val testType: String, // CBC, MSI, Both
    val fileType: String, // Document, Image
    val fileName: String,
    val filePath: String,
    val fileSize: Long = 0,
    val uploadDate: Date = Date(),
    val status: String = "pending", // pending, analyzed, completed
    val notes: String? = null
)
