package com.aiblooddiagnostics.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "doctors")
data class Doctor(
    @PrimaryKey val id: String,
    val name: String,
    val specialization: String,
    val email: String,
    val yearsOfExperience: Int,
    val rating: Float,
    val reviewCount: Int,
    val profileImageUrl: String? = null,
    val focus: String,
    val careerPath: String,
    val highlights: String
) : Parcelable