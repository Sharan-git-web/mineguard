package com.mineinspect.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mineinspect.app.data.local.entity.ObservationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ObservationDao {
    @Upsert
    suspend fun upsert(observation: ObservationEntity)

    @Query("SELECT * FROM observations WHERE inspectionId = :inspectionId AND sectionIndex = :sectionIndex ORDER BY recordedAt")
    fun observeForSection(inspectionId: String, sectionIndex: Int): Flow<List<ObservationEntity>>

    @Query("SELECT * FROM observations WHERE inspectionId = :inspectionId ORDER BY sectionIndex, recordedAt")
    suspend fun getAllForInspection(inspectionId: String): List<ObservationEntity>

    @Query("SELECT * FROM observations WHERE syncState = :state")
    suspend fun getByState(state: String): List<ObservationEntity>

    @Query("SELECT * FROM observations WHERE id = :id")
    suspend fun getById(id: String): ObservationEntity?

    @Query("UPDATE observations SET syncState = :state, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateSyncState(id: String, state: String, updatedAt: Long)

    @Query(
        "UPDATE observations SET syncState = :state, syncAttempts = syncAttempts + 1, " +
            "lastSyncError = :error, updatedAt = :updatedAt WHERE id = :id"
    )
    suspend fun markSyncFailed(id: String, state: String, error: String?, updatedAt: Long)

    @Query("SELECT COUNT(*) FROM observations WHERE inspectionId = :inspectionId")
    fun observeCountForInspection(inspectionId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM observations WHERE inspectionId = :inspectionId AND syncState NOT IN ('SYNCED','PROCESSING','COMPLETED')")
    fun observeUnsyncedCount(inspectionId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM observations WHERE syncState NOT IN ('SYNCED','PROCESSING','COMPLETED')")
    fun observeAllUnsyncedCount(): Flow<Int>

    /** Revives rows that gave up after MAX_SYNC_ATTEMPTS so a manual "Sync now" can retry them. */
    @Query("UPDATE observations SET syncState = 'SYNC_PENDING', syncAttempts = 0, lastSyncError = NULL WHERE syncState = 'SYNC_FAILED'")
    suspend fun resetFailedToPending()

    /** plan §9: a row left in SYNCING by a process death (syncState is set to SYNCING right
     *  before the network dispatch) is otherwise stranded forever — the worker only ever
     *  queries SYNC_PENDING, so nothing picks it up again. Re-sending is safe: every write
     *  endpoint is an idempotent upsert keyed by the client UUID (plan §10/§15). */
    @Query("UPDATE observations SET syncState = 'SYNC_PENDING' WHERE syncState = 'SYNCING'")
    suspend fun resetStuckSyncing()

    @Query("DELETE FROM observations WHERE id = :id AND syncState IN ('LOCAL', 'SYNC_PENDING', 'SYNC_FAILED')")
    suspend fun deleteIfUnsynced(id: String)
}
