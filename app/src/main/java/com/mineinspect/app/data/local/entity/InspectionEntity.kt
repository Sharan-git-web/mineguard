package com.mineinspect.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mineinspect.app.data.local.SyncState

/** Root aggregate of the offline-first schema (plan §5). Primary key is a client-generated UUIDv4 — see plan §10/§15. */
@Entity(
    tableName = "inspections",
    indices = [Index("mineId"), Index("syncState")]
)
data class InspectionEntity(
    @PrimaryKey val id: String,
    val mineId: String,
    val inspectorId: String,
    val status: String = InspectionStatus.DRAFT.name,
    val startedAt: Long,
    val submittedAt: Long? = null,
    val gpsGateResult: String? = null,
    val syncState: String = SyncState.LOCAL.name,
    val syncAttempts: Int = 0,
    val lastSyncError: String? = null,
    val updatedAt: Long
)

enum class InspectionStatus { DRAFT, IN_PROGRESS, SUBMITTED, COMPLETED }
