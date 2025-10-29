package com.aiblooddiagnostics.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey val id: String,
    val fullName: String,
    val age: Int,
    val gender: String,
    val email: String? = null,
    val mobileNumber: String? = null,
    val dateOfBirth: String? = null,
    val bloodType: String? = null,
    val medicalHistory: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable