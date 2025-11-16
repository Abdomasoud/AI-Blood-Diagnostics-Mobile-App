package com.aiblooddiagnostics.di

import android.content.Context
import androidx.room.Room
import com.aiblooddiagnostics.data.dao.*
import com.aiblooddiagnostics.data.database.BloodDiagnosticsDatabase
import com.aiblooddiagnostics.data.manager.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideBloodDiagnosticsDatabase(@ApplicationContext context: Context): BloodDiagnosticsDatabase {
        return Room.databaseBuilder(
            context,
            BloodDiagnosticsDatabase::class.java,
            "blood_diagnostics_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideUserDao(database: BloodDiagnosticsDatabase): UserDao = database.userDao()

    @Provides
    fun provideDoctorDao(database: BloodDiagnosticsDatabase): DoctorDao = database.doctorDao()

    @Provides
    fun providePatientDao(database: BloodDiagnosticsDatabase): PatientDao = database.patientDao()

    @Provides
    fun provideDiagnosisDao(database: BloodDiagnosticsDatabase): DiagnosisDao = database.diagnosisDao()

    @Provides
    fun provideAppointmentDao(database: BloodDiagnosticsDatabase): AppointmentDao = database.appointmentDao()

    @Provides
    fun provideChatDao(database: BloodDiagnosticsDatabase): ChatDao = database.chatDao()

    @Provides
    fun provideDoctorPatientConnectionDao(database: BloodDiagnosticsDatabase): DoctorPatientConnectionDao =
        database.doctorPatientConnectionDao()

    @Provides
    fun provideTestUploadDao(database: BloodDiagnosticsDatabase): TestUploadDao =
        database.testUploadDao()

    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }
}