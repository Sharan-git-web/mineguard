package com.mineinspect.app.data.repository

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.mineinspect.app.data.local.SyncState
import com.mineinspect.app.data.local.dao.ObservationDao
import com.mineinspect.app.data.local.entity.ObservationEntity
import com.mineinspect.app.sync.SyncMetadataWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ObservationRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val observationDao: ObservationDao
) {
    suspend fun addObservation(
        inspectionId: String,
        sectionIndex: Int,
        category: String,
        severity: String,
        notes: String,
        linkedEvidenceId: String? = null,
        gpsPointId: String? = null
    ): String {
        val id = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        observationDao.upsert(
            ObservationEntity(
                id = id,
                inspectionId = inspectionId,
                sectionIndex = sectionIndex,
                category = category,
                severity = severity,
                notes = notes,
                linkedEvidenceId = linkedEvidenceId,
                gpsPointId = gpsPointId,
                recordedAt = now,
                syncState = SyncState.SYNC_PENDING.name,
                updatedAt = now
            )
        )
        enqueueSync()
        return id
    }

    private fun enqueueSync() {
        WorkManager.getInstance(context).enqueueUniqueWork(
            SyncMetadataWorker.UNIQUE_ONE_TIME_WORK_NAME,
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            SyncMetadataWorker.oneTimeRequest()
        )
    }
}
