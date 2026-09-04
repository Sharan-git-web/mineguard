package com.mineinspect.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mineinspect.app.data.local.entity.InspectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InspectionDao {
    @Upsert
    suspend fun upsert(inspection: InspectionEntity)

    @Query("SELECT * FROM inspections WHERE id = :id")
    fun observeById(id: String): Flow<InspectionEntity?>

    @Query("SELECT * FROM inspections WHERE id = :id")
    suspend fun getById(id: String): InspectionEntity?

    @Query("SELECT * FROM inspections WHERE syncState = :state")
    suspend fun getByState(state: String): List<InspectionEntity>

    @Query("UPDATE inspections SET syncState = :state, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateSyncState(id: String, state: String, updatedAt: Long)

    @Query(
        "UPDATE inspections SET syncState = :state, syncAttempts = syncAttempts + 1, " +
            "lastSyncError = :error, updatedAt = :updatedAt WHERE id = :id"
    )
    suspend fun markSyncFailed(id: String, state: String, error: String?, updatedAt: Long)

    @Query("SELECT COUNT(*) FROM inspections WHERE syncState NOT IN ('SYNCED','PROCESSING','COMPLETED')")
    fun observeAllUnsyncedCount(): Flow<Int>

    /** Revives rows that gave up after MAX_SYNC_ATTEMPTS so a manual "Sync now" can retry them. */
    @Query("UPDATE inspections SET syncState = 'SYNC_PENDING', syncAttempts = 0, lastSyncError = NULL WHERE syncState = 'SYNC_FAILED'")
    suspend fun resetFailedToPending()

    /** plan §9: a row left in SYNCING by a process death (syncState is set to SYNCING right
     *  before the network dispatch) is otherwise stranded forever — the worker only ever
     *  queries SYNC_PENDING, so nothing picks it up again. Re-sending is safe: every write
     *  endpoint is an idempotent upsert keyed by the client UUID (plan §10/§15). */
    @Query("UPDATE inspections SET syncState = 'SYNC_PENDING' WHERE syncState = 'SYNCING'")
    suspend fun resetStuckSyncing()

    @Query(
        "UPDATE inspections SET status = :status, syncState = :syncState, " +
            "submittedAt = :submittedAt, updatedAt = :updatedAt WHERE id = :id"
    )
    suspend fun markSubmitted(id: String, status: String, syncState: String, submittedAt: Long, updatedAt: Long)
}
