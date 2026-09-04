package com.mineinspect.app.ui.screens.submission

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.mineinspect.app.data.local.SyncState
import com.mineinspect.app.data.local.dao.EvidenceDao
import com.mineinspect.app.data.local.dao.GpsPointDao
import com.mineinspect.app.data.local.dao.InspectionDao
import com.mineinspect.app.data.local.dao.MeasurementDao
import com.mineinspect.app.data.local.dao.ObservationDao
import com.mineinspect.app.sync.SyncMetadataWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SynchronizationUiState(
    val unsyncedCount: Int = 0,
    val inspectionSyncState: String = SyncState.SYNC_PENDING.name
)

/** Backs SYNCHRONIZATION (and covers OFFLINE_SAVE's intent — when unsyncedCount > 0 and
 *  nothing is actively SYNCING, the screen naturally reads as "saved locally, will sync
 *  when online" rather than needing a separate screen for that state). */
@HiltViewModel
class SynchronizationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val appContext: Context,
    private val inspectionDao: InspectionDao,
    private val evidenceDao: EvidenceDao,
    private val observationDao: ObservationDao,
    private val measurementDao: MeasurementDao,
    private val gpsPointDao: GpsPointDao
) : ViewModel() {

    private val inspectionId: String = checkNotNull(savedStateHandle["inspectionId"])

    val uiState: StateFlow<SynchronizationUiState> = combine(
        combine(
            evidenceDao.observeUnsyncedCount(inspectionId),
            observationDao.observeUnsyncedCount(inspectionId),
            measurementDao.observeUnsyncedCount(inspectionId),
            gpsPointDao.observeUnsyncedCount(inspectionId)
        ) { a, b, c, d -> a + b + c + d },
        inspectionDao.observeById(inspectionId)
    ) { unsynced, inspection ->
        SynchronizationUiState(
            unsyncedCount = unsynced,
            inspectionSyncState = inspection?.syncState ?: SyncState.SYNC_PENDING.name
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SynchronizationUiState())

    fun syncNow() {
        viewModelScope.launch {
            inspectionDao.resetFailedToPending()
            evidenceDao.resetFailedToPending()
            observationDao.resetFailedToPending()
            measurementDao.resetFailedToPending()
            gpsPointDao.resetFailedToPending()
            WorkManager.getInstance(appContext).enqueueUniqueWork(
                SyncMetadataWorker.UNIQUE_ONE_TIME_WORK_NAME,
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                SyncMetadataWorker.oneTimeRequest()
            )
        }
    }
}
