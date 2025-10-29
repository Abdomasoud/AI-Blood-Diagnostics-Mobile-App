package com.aiblooddiagnostics.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey val id: String,
    val patientId: String,
    val doctorId: String,
    val date: String,
    val time: String,
    val duration: String = "30 Minutes",
    val bookingFor: String = "Self",
    val amount: Double = 100.0,
    val currency: String = "EGP",
    val paymentMethod: String = "Card",
    val status: String = "completed", // pending, completed, cancelled
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable