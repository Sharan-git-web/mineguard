package com.mineinspect.app.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.mineinspect.app.data.local.SyncState
import com.mineinspect.app.data.local.UploadState
import com.mineinspect.app.data.local.dao.EvidenceDao
import com.mineinspect.app.data.local.dao.InspectionDao
import com.mineinspect.app.data.remote.EvidenceApi
import com.mineinspect.app.data.remote.dto.ConfirmUploadRequestDto
import com.mineinspect.app.data.remote.dto.EvidenceRegisterRequestDto
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Uploads one evidence photo's binary straight to Supabase Storage via a presigned URL
 * (plan §8, §10), independent of SyncMetadataWorker's JSON row sync. Only proceeds once
 * the evidence row's own metadata has synced (registered server-side), since that's what
 * mints the presigned URL in the first place — re-registering here (via EvidenceApi) is a
 * safe upsert if the URL has since expired.
 *
 * The PUT itself is unauthenticated (the presigned URL carries its own short-lived
 * credential — plan §18/§19), so it deliberately uses a bare OkHttpClient rather than the
 * app's Retrofit client, which would attach an unwanted Authorization header.
 */
@HiltWorker
class EvidenceUploadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val evidenceDao: EvidenceDao,
    private val inspectionDao: InspectionDao,
    private val evidenceApi: EvidenceApi,
    private val okHttpClient: OkHttpClient
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val evidenceId = inputData.getString(KEY_EVIDENCE_ID) ?: return Result.failure()
        val evidence = evidenceDao.getById(evidenceId) ?: return Result.failure()

        if (evidence.uploadState == UploadState.UPLOADED.name) return Result.success()

        val inspection = inspectionDao.getById(evidence.inspectionId) ?: return Result.retry()
        val parentReady = inspection.syncState != SyncState.LOCAL.name &&
            inspection.syncState != SyncState.SYNC_PENDING.name &&
            inspection.syncState != SyncState.SYNCING.name
        if (!parentReady) {
            // Parent Inspection hasn't reached SYNCED+ yet — nothing to upload against.
            return Result.retry()
        }

        return try {
            evidenceDao.updateUploadState(evidence.id, UploadState.UPLOADING.name, evidence.remoteUrl, System.currentTimeMillis())
            val registerResponse = evidenceApi.register(
                EvidenceRegisterRequestDto(
                    id = evidence.id,
                    inspectionId = evidence.inspectionId,
                    sectionIndex = evidence.sectionIndex,
                    capturedAt = evidence.capturedAt,
                    gpsPointId = evidence.gpsPointId,
                    fileHash = evidence.fileHash
                )
            )

            val file = File(evidence.localFilePath)
            withContext(Dispatchers.IO) {
                val request = Request.Builder()
                    .url(registerResponse.uploadUrl)
                    .put(file.asRequestBody("image/jpeg".toMediaType()))
                    .build()
                okHttpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        error("Upload PUT failed: HTTP ${response.code}")
                    }
                }
            }

            val objectPath = "${evidence.inspectionId}/${evidence.id}.jpg"
            evidenceApi.confirmUpload(evidence.id, ConfirmUploadRequestDto(objectPath))
            evidenceDao.updateUploadState(evidence.id, UploadState.UPLOADED.name, objectPath, System.currentTimeMillis())
            Result.success()
        } catch (e: Exception) {
            evidenceDao.updateUploadState(evidence.id, UploadState.UPLOAD_FAILED.name, evidence.remoteUrl, System.currentTimeMillis())
            Result.retry()
        }
    }

    companion object {
        private const val KEY_EVIDENCE_ID = "evidence_id"

        fun requestFor(evidenceId: String): OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<EvidenceUploadWorker>()
                .setInputData(workDataOf(KEY_EVIDENCE_ID to evidenceId))
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                )
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build()

        fun enqueue(context: Context, evidenceId: String) {
            WorkManager.getInstance(context).enqueueUniqueWork(
                "evidence_upload_$evidenceId",
                ExistingWorkPolicy.REPLACE,
                requestFor(evidenceId)
            )
        }
    }
}
