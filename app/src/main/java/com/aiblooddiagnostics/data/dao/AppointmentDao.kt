package com.aiblooddiagnostics.data.dao

import androidx.room.*
import com.aiblooddiagnostics.data.model.Appointment
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments ORDER BY createdAt DESC")
    fun getAllAppointments(): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments WHERE id = :id")
    suspend fun getAppointmentById(id: String): Appointment?

    @Query("SELECT * FROM appointments WHERE patientId = :patientId")
    fun getAppointmentsByPatient(patientId: String): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments WHERE doctorId = :doctorId")
    fun getAppointmentsByDoctor(doctorId: String): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments WHERE status = :status")
    fun getAppointmentsByStatus(status: String): Flow<List<Appointment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: Appointment)

    @Update
    suspend fun updateAppointment(appointment: Appointment)

    @Delete
    suspend fun deleteAppointment(appointment: Appointment)
}