package com.aiblooddiagnostics.data.dao

import androidx.room.*
import com.aiblooddiagnostics.data.model.Patient
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Query("SELECT * FROM patients ORDER BY createdAt DESC")
    fun getAllPatients(): Flow<List<Patient>>

    @Query("SELECT * FROM patients WHERE id = :id")
    suspend fun getPatientById(id: String): Patient?

    @Query("SELECT * FROM patients WHERE fullName LIKE :name")
    fun searchPatientsByName(name: String): Flow<List<Patient>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: Patient)

    @Update
    suspend fun updatePatient(patient: Patient)

    @Delete
    suspend fun deletePatient(patient: Patient)

    @Query("DELETE FROM patients WHERE id = :patientId")
    suspend fun deletePatientById(patientId: String)

    @Query("""
        SELECT DISTINCT p.* FROM patients p 
        INNER JOIN diagnoses d ON p.id = d.patientId 
        WHERE d.doctorId = :doctorId 
        ORDER BY p.createdAt DESC
    """)
    fun getPatientsByDoctor(doctorId: String): Flow<List<Patient>>
}