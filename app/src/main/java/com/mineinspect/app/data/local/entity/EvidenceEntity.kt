package com.mineinspect.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mineinspect.app.data.local.SyncState
import com.mineinspect.app.data.local.UploadState

/**
 * A captured evidence photo (plan §5, §8). Supports multiple rows per section per
 * inspection — this is the direct replacement for the single-URI `CameraState`
 * singleton (plan §22 item 2), wired up in Phase 3.
 */
@Entity(
    tableName = "evidence",
    indices = [Index(value = ["inspectionId", "sectionIndex"]), Index("syncState"), Index("uploadState")]
)
data class EvidenceEntity(
    @PrimaryKey val id: String,
    val inspectionId: String,
    val sectionIndex: Int,
    val localFilePath: String,
    val remoteUrl: String? = null,
    val capturedAt: Long,
    val gpsPointId: String? = null,
    val inspectorId: String,
    val fileHash: String? = null,
    val uploadState: String = UploadState.NOT_UPLOADED.name,
    val syncState: String = SyncState.LOCAL.name,
    val syncAttempts: Int = 0,
    val lastSyncError: String? = null,
    val updatedAt: Long
)
