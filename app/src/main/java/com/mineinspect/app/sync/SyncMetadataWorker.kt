package com.mineinspect.app.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.mineinspect.app.data.local.SyncState
import com.mineinspect.app.data.local.dao.EvidenceDao
import com.mineinspect.app.data.local.dao.GpsPointDao
import com.mineinspect.app.data.local.dao.InspectionDao
import com.mineinspect.app.data.local.dao.MeasurementDao
import com.mineinspect.app.data.local.dao.ObservationDao
import com.mineinspect.app.data.local.entity.EvidenceEntity
import com.mineinspect.app.data.local.entity.GpsPointEntity
import com.mineinspect.app.data.local.entity.MeasurementEntity
import com.mineinspect.app.data.local.entity.ObservationEntity
import com.mineinspect.app.data.remote.EvidenceApi
import com.mineinspect.app.data.remote.GpsPointApi
import com.mineinspect.app.data.remote.InspectionApi
import com.mineinspect.app.data.remote.MeasurementApi
import com.mineinspect.app.data.remote.ObservationApi
import com.mineinspect.app.data.remote.dto.CreateInspectionRequestDto
import com.mineinspect.app.data.remote.dto.EvidenceRegisterRequestDto
import com.mineinspect.app.data.remote.dto.GpsPointDto
import com.mineinspect.app.data.remote.dto.GpsPointsBatchRequestDto
import com.mineinspect.app.data.remote.dto.MeasurementRequestDto
import com.mineinspect.app.data.remote.dto.ObservationRequestDto
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

/**
 * Syncs Inspection/GpsPoint/Observation/Measurement/Evidence-metadata rows in dependency
 * order (plan §10): Inspection first, since children reference inspectionId, then the
 * rest. Evidence's binary upload is handled separately by EvidenceUploadWorker, chained
 * after the evidence row's metadata syncs here.
 */
@HiltWorker
class SyncMetadataWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val inspectionDao: InspectionDao,
    private val inspectionApi: InspectionApi,
    private val gpsPointDao: GpsPointDao,
    private val gpsPointApi: GpsPointApi,
    private val observationDao: ObservationDao,
    private val observationApi: ObservationApi,
    private val measurementDao: MeasurementDao,
    private val measurementApi: MeasurementApi,
    private val evidenceDao: EvidenceDao,
    private val evidenceApi: EvidenceApi
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        recoverStuckSyncingRows()

        val inspectionsRetry = syncPendingInspections()
        val gpsRetry = syncPendingGpsPoints()
        val observationsRetry = syncPendingObservations()
        val measurementsRetry = syncPendingMeasurements()
        val evidenceRetry = syncPendingEvidenceMetadata()

        val shouldRetry = inspectionsRetry || gpsRetry || observationsRetry || measurementsRetry || evidenceRetry
        return if (shouldRetry) Result.retry() else Result.success()
    }

    /**
     * plan §9's promised "startup check", which was never actually implemented: syncState is
     * set to SYNCING in the same step as the network dispatch, so a process death (or a crash
     * mid-request) leaves the row in SYNCING permanently. Nothing queries SYNCING — the worker
     * only ever looks for SYNC_PENDING — so those rows are stranded and silently never reach
     * the server. Re-sending them is safe by construction: every write endpoint is an
     * idempotent upsert keyed by the client-generated UUID (plan §10/§15).
     */
    private suspend fun recoverStuckSyncingRows() {
        inspectionDao.resetStuckSyncing()
        gpsPointDao.resetStuckSyncing()
        observationDao.resetStuckSyncing()
        measurementDao.resetStuckSyncing()
        evidenceDao.resetStuckSyncing()
    }

    private suspend fun syncPendingInspections(): Boolean {
        val pending = inspectionDao.getByState(SyncState.SYNC_PENDING.name)
        var shouldRetry = false
        for (inspection in pending) {
            inspectionDao.updateSyncState(inspection.id, SyncState.SYNCING.name, System.currentTimeMillis())
            try {
                inspectionApi.createInspection(
                    CreateInspectionRequestDto(
                        id = inspection.id,
                        mineId = inspection.mineId,
                        inspectorId = inspection.inspectorId,
                        startedAt = inspection.startedAt,
                        gpsGateResult = inspection.gpsGateResult
                    )
                )
                inspectionDao.updateSyncState(inspection.id, SyncState.SYNCED.name, System.currentTimeMillis())
            } catch (e: Exception) {
                val now = System.currentTimeMillis()
                inspectionDao.markSyncFailed(inspection.id, SyncState.SYNC_PENDING.name, e.message, now)
                val updated = inspectionDao.getById(inspection.id)
                if (updated != null && updated.syncAttempts >= MAX_SYNC_ATTEMPTS) {
                    inspectionDao.updateSyncState(inspection.id, SyncState.SYNC_FAILED.name, now)
                } else {
                    shouldRetry = true
                }
            }
        }
        return shouldRetry
    }

    /** GPS points sync via the batched endpoint (plan §12-13 #6), grouped by inspection. */
    private suspend fun syncPendingGpsPoints(): Boolean {
        val pending = gpsPointDao.getByState(SyncState.SYNC_PENDING.name)
        if (pending.isEmpty()) return false

        var shouldRetry = false
        for ((inspectionId, points) in pending.groupBy { it.inspectionId }) {
            val now = System.currentTimeMillis()
            points.forEach { gpsPointDao.updateSyncState(it.id, SyncState.SYNCING.name, now) }
            try {
                gpsPointApi.upsertBatch(
                    inspectionId,
                    GpsPointsBatchRequestDto(points.map { it.toDto() })
                )
                val syncedAt = System.currentTimeMillis()
                points.forEach { gpsPointDao.updateSyncState(it.id, SyncState.SYNCED.name, syncedAt) }
            } catch (e: Exception) {
                val failedAt = System.currentTimeMillis()
                for (point in points) {
                    gpsPointDao.markSyncFailed(point.id, SyncState.SYNC_PENDING.name, e.message, failedAt)
                    val updated = gpsPointDao.getById(point.id)
                    if (updated != null && updated.syncAttempts >= MAX_SYNC_ATTEMPTS) {
                        gpsPointDao.updateSyncState(point.id, SyncState.SYNC_FAILED.name, failedAt)
                    } else {
                        shouldRetry = true
                    }
                }
            }
        }
        return shouldRetry
    }

    private suspend fun syncPendingObservations(): Boolean {
        val pending = observationDao.getByState(SyncState.SYNC_PENDING.name)
        var shouldRetry = false
        for (observation in pending) {
            observationDao.updateSyncState(observation.id, SyncState.SYNCING.name, System.currentTimeMillis())
            try {
                observationApi.upsert(observation.inspectionId, observation.toDto())
                observationDao.updateSyncState(observation.id, SyncState.SYNCED.name, System.currentTimeMillis())
            } catch (e: Exception) {
                val now = System.currentTimeMillis()
                observationDao.markSyncFailed(observation.id, SyncState.SYNC_PENDING.name, e.message, now)
                val updated = observationDao.getById(observation.id)
                if (updated != null && updated.syncAttempts >= MAX_SYNC_ATTEMPTS) {
                    observationDao.updateSyncState(observation.id, SyncState.SYNC_FAILED.name, now)
                } else {
                    shouldRetry = true
                }
            }
        }
        return shouldRetry
    }

    private suspend fun syncPendingMeasurements(): Boolean {
        val pending = measurementDao.getByState(SyncState.SYNC_PENDING.name)
        var shouldRetry = false
        for (measurement in pending) {
            measurementDao.updateSyncState(measurement.id, SyncState.SYNCING.name, System.currentTimeMillis())
            try {
                val response = measurementApi.upsert(measurement.inspectionId, measurement.toDto())
                // thresholdStatus is server-authoritative — cache whatever it returns, even null (plan §17).
                measurementDao.upsert(measurement.copy(thresholdStatus = response.thresholdStatus))
                measurementDao.updateSyncState(measurement.id, SyncState.SYNCED.name, System.currentTimeMillis())
            } catch (e: Exception) {
                val now = System.currentTimeMillis()
                measurementDao.markSyncFailed(measurement.id, SyncState.SYNC_PENDING.name, e.message, now)
                val updated = measurementDao.getById(measurement.id)
                if (updated != null && updated.syncAttempts >= MAX_SYNC_ATTEMPTS) {
                    measurementDao.updateSyncState(measurement.id, SyncState.SYNC_FAILED.name, now)
                } else {
                    shouldRetry = true
                }
            }
        }
        return shouldRetry
    }

    /** Registers evidence metadata + mints the upload URL; EvidenceUploadWorker does the
     *  actual binary PUT once this row is SYNCED (plan §8/§10). */
    private suspend fun syncPendingEvidenceMetadata(): Boolean {
        val pending = evidenceDao.getByState(SyncState.SYNC_PENDING.name)
        var shouldRetry = false
        for (evidence in pending) {
            evidenceDao.updateSyncState(evidence.id, SyncState.SYNCING.name, System.currentTimeMillis())
            try {
                evidenceApi.register(evidence.toRegisterDto())
                evidenceDao.updateSyncState(evidence.id, SyncState.SYNCED.name, System.currentTimeMillis())
                EvidenceUploadWorker.enqueue(applicationContext, evidence.id)
            } catch (e: Exception) {
                val now = System.currentTimeMillis()
                evidenceDao.markSyncFailed(evidence.id, SyncState.SYNC_PENDING.name, e.message, now)
                val updated = evidenceDao.getById(evidence.id)
                if (updated != null && updated.syncAttempts >= MAX_SYNC_ATTEMPTS) {
                    evidenceDao.updateSyncState(evidence.id, SyncState.SYNC_FAILED.name, now)
                } else {
                    shouldRetry = true
                }
            }
        }
        return shouldRetry
    }

    companion object {
        /** plan §10: a row is only left terminally SYNC_FAILED once attempts exceed this. */
        const val MAX_SYNC_ATTEMPTS = 5

        const val UNIQUE_ONE_TIME_WORK_NAME = "sync_metadata_worker_one_time"
        const val UNIQUE_PERIODIC_WORK_NAME = "sync_metadata_worker_periodic"

        fun oneTimeRequest(): OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<SyncMetadataWorker>()
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                )
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build()

        fun periodicRequest(): PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<SyncMetadataWorker>(15, TimeUnit.MINUTES)
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                )
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build()
    }
}

private fun GpsPointEntity.toDto() = GpsPointDto(
    id = id,
    sectionIndex = sectionIndex,
    latitude = latitude,
    longitude = longitude,
    accuracyMeters = accuracyMeters,
    source = source,
    capturedAt = capturedAt
)

private fun ObservationEntity.toDto() = ObservationRequestDto(
    id = id,
    sectionIndex = sectionIndex,
    category = category,
    severity = severity,
    notes = notes,
    linkedEvidenceId = linkedEvidenceId,
    gpsPointId = gpsPointId,
    recordedAt = recordedAt
)

private fun MeasurementEntity.toDto() = MeasurementRequestDto(
    id = id,
    sectionIndex = sectionIndex,
    metricType = metricType,
    value = value,
    unit = unit,
    recordedAt = recordedAt
)

private fun EvidenceEntity.toRegisterDto() = EvidenceRegisterRequestDto(
    id = id,
    inspectionId = inspectionId,
    sectionIndex = sectionIndex,
    capturedAt = capturedAt,
    gpsPointId = gpsPointId,
    fileHash = fileHash
)
