package com.mineinspect.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mineinspect.app.data.local.entity.MeasurementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {
    @Upsert
    suspend fun upsert(measurement: MeasurementEntity)

    @Query("SELECT * FROM measurements WHERE inspectionId = :inspectionId AND sectionIndex = :sectionIndex ORDER BY recordedAt")
    fun observeForSection(inspectionId: String, sectionIndex: Int): Flow<List<MeasurementEntity>>

    @Query("SELECT * FROM measurements WHERE inspectionId = :inspectionId ORDER BY sectionIndex, recordedAt")
    suspend fun getAllForInspection(inspectionId: String): List<MeasurementEntity>

    @Query("SELECT * FROM measurements WHERE syncState = :state")
    suspend fun getByState(state: String): List<MeasurementEntity>

    @Query("SELECT * FROM measurements WHERE id = :id")
    suspend fun getById(id: String): MeasurementEntity?

    @Query("UPDATE measurements SET syncState = :state, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateSyncState(id: String, state: String, updatedAt: Long)

    @Query(
        "UPDATE measurements SET syncState = :state, syncAttempts = syncAttempts + 1, " +
            "lastSyncError = :error, updatedAt = :updatedAt WHERE id = :id"
    )
    suspend fun markSyncFailed(id: String, state: String, error: String?, updatedAt: Long)

    @Query("SELECT COUNT(*) FROM measurements WHERE inspectionId = :inspectionId")
    fun observeCountForInspection(inspectionId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM measurements WHERE inspectionId = :inspectionId AND syncState NOT IN ('SYNCED','PROCESSING','COMPLETED')")
    fun observeUnsyncedCount(inspectionId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM measurements WHERE syncState NOT IN ('SYNCED','PROCESSING','COMPLETED')")
    fun observeAllUnsyncedCount(): Flow<Int>

    /** Revives rows that gave up after MAX_SYNC_ATTEMPTS so a manual "Sync now" can retry them. */
    @Query("UPDATE measurements SET syncState = 'SYNC_PENDING', syncAttempts = 0, lastSyncError = NULL WHERE syncState = 'SYNC_FAILED'")
    suspend fun resetFailedToPending()

    /** plan §9: a row left in SYNCING by a process death (syncState is set to SYNCING right
     *  before the network dispatch) is otherwise stranded forever — the worker only ever
     *  queries SYNC_PENDING, so nothing picks it up again. Re-sending is safe: every write
     *  endpoint is an idempotent upsert keyed by the client UUID (plan §10/§15). */
    @Query("UPDATE measurements SET syncState = 'SYNC_PENDING' WHERE syncState = 'SYNCING'")
    suspend fun resetStuckSyncing()

    @Query("DELETE FROM measurements WHERE id = :id AND syncState IN ('LOCAL', 'SYNC_PENDING', 'SYNC_FAILED')")
    suspend fun deleteIfUnsynced(id: String)
}
