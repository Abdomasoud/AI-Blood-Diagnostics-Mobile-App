package com.aiblooddiagnostics.data.database

import androidx.room.*
import com.aiblooddiagnostics.data.dao.*
import com.aiblooddiagnostics.data.model.*

@Database(
    entities = [
        User::class,
        Doctor::class,
        Patient::class,
        Diagnosis::class,
        Appointment::class,
        ChatMessage::class,
        ChatRoom::class,
        DoctorPatientConnection::class,
        TestUpload::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BloodDiagnosticsDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun doctorDao(): DoctorDao
    abstract fun patientDao(): PatientDao
    abstract fun diagnosisDao(): DiagnosisDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun chatDao(): ChatDao
    abstract fun doctorPatientConnectionDao(): DoctorPatientConnectionDao
    abstract fun testUploadDao(): TestUploadDao
}