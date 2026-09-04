package com.mineinspect.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mineinspect.app.data.local.SyncState

/** Backs the still-unbuilt MEASUREMENT_ENTRY route (plan §5, Phase 4). `thresholdStatus` is a
 *  cached server-computed display value only — never evaluated on-device (plan §17). */
@Entity(
    tableName = "measurements",
    indices = [Index(value = ["inspectionId", "sectionIndex"]), Index("syncState")]
)
data class MeasurementEntity(
    @PrimaryKey val id: String,
    val inspectionId: String,
    val sectionIndex: Int,
    val metricType: String,
    val value: Double,
    val unit: String,
    val thresholdStatus: String? = null,
    val recordedAt: Long,
    val syncState: String = SyncState.LOCAL.name,
    val syncAttempts: Int = 0,
    val lastSyncError: String? = null,
    val updatedAt: Long
)
