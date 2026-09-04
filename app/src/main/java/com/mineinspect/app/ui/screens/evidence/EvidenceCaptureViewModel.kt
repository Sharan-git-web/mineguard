package com.mineinspect.app.ui.screens.evidence

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.mineinspect.app.data.local.SyncState
import com.mineinspect.app.data.local.dao.EvidenceDao
import com.mineinspect.app.data.local.dao.GpsPointDao
import com.mineinspect.app.data.local.entity.EvidenceEntity
import com.mineinspect.app.data.security.TokenStore
import com.mineinspect.app.sync.SyncMetadataWorker
import com.mineinspect.app.util.sectionIndexOf
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject

data class EvidenceCaptureUiState(
    val capturedUri: Uri? = null,
    val isSaving: Boolean = false
)

/**
 * Replaces the CameraState singleton (plan §8, §22 item 2) — each captured photo becomes
 * its own EvidenceEntity row instead of overwriting a single in-memory URI, so multiple
 * photos per section are no longer silently lost.
 */
@HiltViewModel
class EvidenceCaptureViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val appContext: Context,
    private val evidenceDao: EvidenceDao,
    private val gpsPointDao: GpsPointDao,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val inspectionId: String = checkNotNull(savedStateHandle["inspectionId"])
    private val sectionId: String = checkNotNull(savedStateHandle["sectionId"])
    private val sectionIndex = sectionIndexOf(sectionId)

    private val _uiState = MutableStateFlow(EvidenceCaptureUiState())
    val uiState: StateFlow<EvidenceCaptureUiState> = _uiState.asStateFlow()

    private val _evidenceSaved = MutableSharedFlow<String>()
    val evidenceSaved: SharedFlow<String> = _evidenceSaved.asSharedFlow()

    fun onPhotoCaptured(uri: Uri, file: File) {
        _uiState.update { it.copy(capturedUri = uri, isSaving = true) }
        viewModelScope.launch {
            val hash = computeSha256(file)
            val gpsPoint = gpsPointDao.getMostRecent(inspectionId)
            val id = UUID.randomUUID().toString()
            val now = System.currentTimeMillis()
            evidenceDao.upsert(
                EvidenceEntity(
                    id = id,
                    inspectionId = inspectionId,
                    sectionIndex = sectionIndex,
                    localFilePath = file.absolutePath,
                    capturedAt = now,
                    gpsPointId = gpsPoint?.id,
                    inspectorId = tokenStore.getInspectorId() ?: "",
                    fileHash = hash,
                    syncState = SyncState.SYNC_PENDING.name,
                    updatedAt = now
                )
            )
            enqueueSync()
            _uiState.update { it.copy(isSaving = false) }
            _evidenceSaved.emit(id)
        }
    }

    private suspend fun computeSha256(file: File): String = withContext(Dispatchers.IO) {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(8192)
            var read: Int
            while (input.read(buffer).also { read = it } != -1) {
                digest.update(buffer, 0, read)
            }
        }
        digest.digest().joinToString("") { "%02x".format(it) }
    }

    private fun enqueueSync() {
        WorkManager.getInstance(appContext).enqueueUniqueWork(
            SyncMetadataWorker.UNIQUE_ONE_TIME_WORK_NAME,
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            SyncMetadataWorker.oneTimeRequest()
        )
    }
}
