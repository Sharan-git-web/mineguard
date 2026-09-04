package com.mineinspect.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mineinspect.app.data.local.SyncState

/** A single real GPS fix — gate check, section entry, breadcrumb, or hazard marker (plan §5, §7). */
@Entity(
    tableName = "gps_points",
    indices = [Index("inspectionId"), Index("syncState")]
)
data class GpsPointEntity(
    @PrimaryKey val id: String,
    val inspectionId: String,
    val sectionIndex: Int? = null,
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float,
    val source: String = GpsPointSource.GPS_GATE.name,
    val capturedAt: Long,
    val syncState: String = SyncState.LOCAL.name,
    val syncAttempts: Int = 0,
    val lastSyncError: String? = null,
    val updatedAt: Long
)

enum class GpsPointSource { GPS_GATE, SECTION_ENTRY, BREADCRUMB, HAZARD_MARKER }
