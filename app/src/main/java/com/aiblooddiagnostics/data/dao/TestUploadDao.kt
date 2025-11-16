package com.aiblooddiagnostics.data.dao

import androidx.room.*
import com.aiblooddiagnostics.data.model.TestUpload
import kotlinx.coroutines.flow.Flow

@Dao
interface TestUploadDao {
    
    @Query("SELECT * FROM test_uploads WHERE patientId = :patientId ORDER BY uploadDate DESC")
    fun getPatientUploads(patientId: String): Flow<List<TestUpload>>
    
    @Query("SELECT * FROM test_uploads WHERE id = :uploadId")
    suspend fun getUploadById(uploadId: Int): TestUpload?
    
    @Query("SELECT * FROM test_uploads WHERE status = :status ORDER BY uploadDate DESC")
    fun getUploadsByStatus(status: String): Flow<List<TestUpload>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpload(upload: TestUpload): Long
    
    @Update
    suspend fun updateUpload(upload: TestUpload)
    
    @Delete
    suspend fun deleteUpload(upload: TestUpload)
    
    @Query("UPDATE test_uploads SET status = :status WHERE id = :uploadId")
    suspend fun updateUploadStatus(uploadId: Int, status: String)
    
    @Query("DELETE FROM test_uploads WHERE patientId = :patientId")
    suspend fun deletePatientUploads(patientId: String)
}
