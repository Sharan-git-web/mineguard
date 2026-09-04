package com.mineinspect.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mineinspect.app.data.local.SyncState

/** Backs the still-unbuilt MANUAL_OBSERVATION / ANOMALY_WARNING routes (plan §5, Phase 4). */
@Entity(
    tableName = "observations",
    indices = [Index(value = ["inspectionId", "sectionIndex"]), Index("syncState")]
)
data class ObservationEntity(
    @PrimaryKey val id: String,
    val inspectionId: String,
    val sectionIndex: Int,
    val category: String,
    val severity: String, // reuses ui.components.Severity enum names: LOW/MED/HIGH/CRITICAL
    val notes: String,
    val linkedEvidenceId: String? = null,
    val gpsPointId: String? = null,
    val recordedAt: Long,
    val syncState: String = SyncState.LOCAL.name,
    val syncAttempts: Int = 0,
    val lastSyncError: String? = null,
    val updatedAt: Long
)
