package com.aiblooddiagnostics.data.dao

import androidx.room.*
import com.aiblooddiagnostics.data.model.DoctorPatientConnection
import kotlinx.coroutines.flow.Flow

@Dao
interface DoctorPatientConnectionDao {
    
    @Query("SELECT * FROM doctor_patient_connections WHERE patientId = :patientId")
    fun getPatientConnections(patientId: String): Flow<List<DoctorPatientConnection>>
    
    @Query("SELECT * FROM doctor_patient_connections WHERE doctorId = :doctorId")
    fun getDoctorConnections(doctorId: String): Flow<List<DoctorPatientConnection>>
    
    @Query("SELECT * FROM doctor_patient_connections WHERE patientId = :patientId AND doctorId = :doctorId LIMIT 1")
    suspend fun getConnection(patientId: String, doctorId: String): DoctorPatientConnection?
    
    @Query("SELECT * FROM doctor_patient_connections WHERE doctorId = :doctorId AND status = 'pending'")
    fun getPendingRequests(doctorId: String): Flow<List<DoctorPatientConnection>>
    
    @Query("SELECT * FROM doctor_patient_connections WHERE doctorId = :doctorId AND status = 'approved'")
    fun getApprovedConnections(doctorId: String): Flow<List<DoctorPatientConnection>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConnection(connection: DoctorPatientConnection): Long
    
    @Update
    suspend fun updateConnection(connection: DoctorPatientConnection)
    
    @Delete
    suspend fun deleteConnection(connection: DoctorPatientConnection)
    
    @Query("UPDATE doctor_patient_connections SET status = :status, approvalDate = :approvalDate WHERE id = :connectionId")
    suspend fun updateConnectionStatus(connectionId: Int, status: String, approvalDate: Long?)
    
    @Query("DELETE FROM doctor_patient_connections WHERE patientId = :patientId AND doctorId = :doctorId")
    suspend fun deleteConnectionByIds(patientId: String, doctorId: String)
}
