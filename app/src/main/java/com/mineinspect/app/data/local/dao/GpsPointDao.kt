package com.mineinspect.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mineinspect.app.data.local.entity.GpsPointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GpsPointDao {
    @Upsert
    suspend fun upsert(point: GpsPointEntity)

    @Upsert
    suspend fun upsertAll(points: List<GpsPointEntity>)

    @Query("SELECT * FROM gps_points WHERE inspectionId = :inspectionId ORDER BY capturedAt")
    fun observeForInspection(inspectionId: String): Flow<List<GpsPointEntity>>

    @Query("SELECT * FROM gps_points WHERE inspectionId = :inspectionId ORDER BY capturedAt")
    suspend fun getAllForInspection(inspectionId: String): List<GpsPointEntity>

    @Query("SELECT * FROM gps_points WHERE inspectionId = :inspectionId ORDER BY capturedAt DESC LIMIT 1")
    suspend fun getMostRecent(inspectionId: String): GpsPointEntity?

    @Query("SELECT * FROM gps_points WHERE id = :id")
    suspend fun getById(id: String): GpsPointEntity?

    @Query("SELECT COUNT(*) FROM gps_points WHERE inspectionId = :inspectionId")
    fun observeCountForInspection(inspectionId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM gps_points WHERE inspectionId = :inspectionId AND syncState NOT IN ('SYNCED','PROCESSING','COMPLETED')")
    fun observeUnsyncedCount(inspectionId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM gps_points WHERE syncState NOT IN ('SYNCED','PROCESSING','COMPLETED')")
    fun observeAllUnsyncedCount(): Flow<Int>

    @Query("SELECT * FROM gps_points WHERE syncState = :state")
    suspend fun getByState(state: String): List<GpsPointEntity>

    /** Revives rows that gave up after MAX_SYNC_ATTEMPTS so a manual "Sync now" can retry them. */
    @Query("UPDATE gps_points SET syncState = 'SYNC_PENDING', syncAttempts = 0, lastSyncError = NULL WHERE syncState = 'SYNC_FAILED'")
    suspend fun resetFailedToPending()

    /** plan §9: a row left in SYNCING by a process death (syncState is set to SYNCING right
     *  before the network dispatch) is otherwise stranded forever — the worker only ever
     *  queries SYNC_PENDING, so nothing picks it up again. Re-sending is safe: every write
     *  endpoint is an idempotent upsert keyed by the client UUID (plan §10/§15). */
    @Query("UPDATE gps_points SET syncState = 'SYNC_PENDING' WHERE syncState = 'SYNCING'")
    suspend fun resetStuckSyncing()

    @Query("UPDATE gps_points SET syncState = :state, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateSyncState(id: String, state: String, updatedAt: Long)

    @Query(
        "UPDATE gps_points SET syncState = :state, syncAttempts = syncAttempts + 1, " +
            "lastSyncError = :error, updatedAt = :updatedAt WHERE id = :id"
    )
    suspend fun markSyncFailed(id: String, state: String, error: String?, updatedAt: Long)
}
