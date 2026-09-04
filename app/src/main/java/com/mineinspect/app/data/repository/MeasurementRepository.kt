package com.mineinspect.app.data.repository

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.mineinspect.app.data.local.SyncState
import com.mineinspect.app.data.local.dao.MeasurementDao
import com.mineinspect.app.data.local.entity.MeasurementEntity
import com.mineinspect.app.sync.SyncMetadataWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MeasurementRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val measurementDao: MeasurementDao
) {
    suspend fun addMeasurement(
        inspectionId: String,
        sectionIndex: Int,
        metricType: String,
        value: Double,
        unit: String
    ): String {
        val id = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        measurementDao.upsert(
            MeasurementEntity(
                id = id,
                inspectionId = inspectionId,
                sectionIndex = sectionIndex,
                metricType = metricType,
                value = value,
                unit = unit,
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
