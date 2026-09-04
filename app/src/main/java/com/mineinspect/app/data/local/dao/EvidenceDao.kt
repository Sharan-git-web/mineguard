package com.mineinspect.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mineinspect.app.data.local.entity.EvidenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EvidenceDao {
    @Upsert
    suspend fun upsert(evidence: EvidenceEntity)

    @Query("SELECT * FROM evidence WHERE inspectionId = :inspectionId AND sectionIndex = :sectionIndex ORDER BY capturedAt")
    fun observeForSection(inspectionId: String, sectionIndex: Int): Flow<List<EvidenceEntity>>

    @Query("SELECT * FROM evidence WHERE inspectionId = :inspectionId ORDER BY sectionIndex, capturedAt")
    suspend fun getAllForInspection(inspectionId: String): List<EvidenceEntity>

    /** Derives section photo-count from real rows instead of a separate counter — fixes the double-increment bug (plan §22 item 1). */
    @Query("SELECT COUNT(*) FROM evidence WHERE inspectionId = :inspectionId AND sectionIndex = :sectionIndex")
    fun observePhotoCount(inspectionId: String, sectionIndex: Int): Flow<Int>

    @Query("SELECT * FROM evidence WHERE syncState = :state")
    suspend fun getByState(state: String): List<EvidenceEntity>

    @Query("SELECT * FROM evidence WHERE uploadState = :state")
    suspend fun getByUploadState(state: String): List<EvidenceEntity>

    @Query("SELECT * FROM evidence WHERE id = :id")
    suspend fun getById(id: String): EvidenceEntity?

    @Query("UPDATE evidence SET syncState = :state, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateSyncState(id: String, state: String, updatedAt: Long)

    @Query(
        "UPDATE evidence SET syncState = :state, syncAttempts = syncAttempts + 1, " +
            "lastSyncError = :error, updatedAt = :updatedAt WHERE id = :id"
    )
    suspend fun markSyncFailed(id: String, state: String, error: String?, updatedAt: Long)

    @Query("UPDATE evidence SET uploadState = :state, remoteUrl = :remoteUrl, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateUploadState(id: String, state: String, remoteUrl: String?, updatedAt: Long)

    @Query("SELECT COUNT(*) FROM evidence WHERE inspectionId = :inspectionId")
    fun observeCountForInspection(inspectionId: String): Flow<Int>

    @Query(
        "SELECT COUNT(*) FROM evidence WHERE inspectionId = :inspectionId AND " +
            "(syncState NOT IN ('SYNCED','PROCESSING','COMPLETED') OR uploadState != 'UPLOADED')"
    )
    fun observeUnsyncedCount(inspectionId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM evidence WHERE syncState NOT IN ('SYNCED','PROCESSING','COMPLETED') OR uploadState != 'UPLOADED'")
    fun observeAllUnsyncedCount(): Flow<Int>

    /** Revives rows that gave up after MAX_SYNC_ATTEMPTS so a manual "Sync now" can retry them. */
    @Query("UPDATE evidence SET syncState = 'SYNC_PENDING', syncAttempts = 0, lastSyncError = NULL WHERE syncState = 'SYNC_FAILED'")
    suspend fun resetFailedToPending()

    /** plan §9: a row left in SYNCING by a process death (syncState is set to SYNCING right
     *  before the network dispatch) is otherwise stranded forever — the worker only ever
     *  queries SYNC_PENDING, so nothing picks it up again. Re-sending is safe: every write
     *  endpoint is an idempotent upsert keyed by the client UUID (plan §10/§15). */
    @Query("UPDATE evidence SET syncState = 'SYNC_PENDING' WHERE syncState = 'SYNCING'")
    suspend fun resetStuckSyncing()

    /** Only unsynced rows on a not-yet-submitted inspection may be deleted (plan §6). */
    @Query("DELETE FROM evidence WHERE id = :id AND syncState IN ('LOCAL', 'SYNC_PENDING', 'SYNC_FAILED')")
    suspend fun deleteIfUnsynced(id: String)
}
