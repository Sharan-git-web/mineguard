package com.mineinspect.app.data.repository

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.mineinspect.app.data.local.SyncState
import com.mineinspect.app.data.local.dao.GpsPointDao
import com.mineinspect.app.data.local.entity.GpsPointEntity
import com.mineinspect.app.data.local.entity.GpsPointSource
import com.mineinspect.app.data.location.LocationFix
import com.mineinspect.app.sync.SyncMetadataWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/** Turns a real LocationFix into a queued GpsPointEntity row (plan §5, §7, §9). */
@Singleton
class GpsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gpsPointDao: GpsPointDao
) {
    suspend fun recordFix(
        inspectionId: String,
        sectionIndex: Int?,
        fix: LocationFix,
        source: GpsPointSource
    ): GpsPointEntity {
        val entity = GpsPointEntity(
            id = UUID.randomUUID().toString(),
            inspectionId = inspectionId,
            sectionIndex = sectionIndex,
            latitude = fix.latitude,
            longitude = fix.longitude,
            accuracyMeters = fix.accuracyMeters,
            source = source.name,
            capturedAt = fix.capturedAt,
            syncState = SyncState.SYNC_PENDING.name,
            updatedAt = fix.capturedAt
        )
        gpsPointDao.upsert(entity)
        enqueueSync()
        return entity
    }

    private fun enqueueSync() {
        WorkManager.getInstance(context).enqueueUniqueWork(
            SyncMetadataWorker.UNIQUE_ONE_TIME_WORK_NAME,
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            SyncMetadataWorker.oneTimeRequest()
        )
    }
}
