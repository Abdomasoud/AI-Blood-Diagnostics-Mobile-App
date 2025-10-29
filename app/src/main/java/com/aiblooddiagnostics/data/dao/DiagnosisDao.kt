package com.aiblooddiagnostics.data.dao

import androidx.room.*
import com.aiblooddiagnostics.data.model.Diagnosis
import kotlinx.coroutines.flow.Flow

@Dao
interface DiagnosisDao {
    @Query("SELECT * FROM diagnoses ORDER BY createdAt DESC")
    fun getAllDiagnoses(): Flow<List<Diagnosis>>

    @Query("SELECT * FROM diagnoses WHERE id = :id")
    suspend fun getDiagnosisById(id: String): Diagnosis?

    @Query("SELECT * FROM diagnoses WHERE patientId = :patientId")
    fun getDiagnosesByPatient(patientId: String): Flow<List<Diagnosis>>

    @Query("SELECT * FROM diagnoses WHERE doctorId = :doctorId")
    fun getDiagnosesByDoctor(doctorId: String): Flow<List<Diagnosis>>

    @Query("SELECT * FROM diagnoses WHERE status = :status")
    fun getDiagnosesByStatus(status: String): Flow<List<Diagnosis>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiagnosis(diagnosis: Diagnosis)

    @Update
    suspend fun updateDiagnosis(diagnosis: Diagnosis)

    @Delete
    suspend fun deleteDiagnosis(diagnosis: Diagnosis)

    @Query("DELETE FROM diagnoses WHERE patientId = :patientId")
    suspend fun deleteDiagnosesByPatient(patientId: String)
}