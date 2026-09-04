package com.mineinspect.app.data.repository

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.mineinspect.app.data.local.SyncState
import com.mineinspect.app.data.local.dao.InspectionDao
import com.mineinspect.app.data.local.entity.InspectionEntity
import com.mineinspect.app.data.local.entity.InspectionStatus
import com.mineinspect.app.sync.SyncMetadataWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Owns the local Inspection lifecycle. Creating an inspection is a local, offline-capable
 * write — the row is queued for sync immediately (plan §9/§10); the app never blocks on
 * network here, and the flow continues fully offline against the local row.
 */
@Singleton
class InspectionRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val inspectionDao: InspectionDao
) {
    fun observeInspection(id: String): Flow<InspectionEntity?> = inspectionDao.observeById(id)

    suspend fun startInspection(mineId: String, inspectorId: String, gpsGateResult: String? = null): String {
        val id = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        val inspection = InspectionEntity(
            id = id,
            mineId = mineId,
            inspectorId = inspectorId,
            status = InspectionStatus.IN_PROGRESS.name,
            startedAt = now,
            gpsGateResult = gpsGateResult,
            syncState = SyncState.SYNC_PENDING.name,
            updatedAt = now
        )
        inspectionDao.upsert(inspection)
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
