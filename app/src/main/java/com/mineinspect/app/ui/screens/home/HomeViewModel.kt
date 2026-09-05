package com.mineinspect.app.ui.screens.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.mineinspect.app.data.local.dao.EvidenceDao
import com.mineinspect.app.data.local.dao.GpsPointDao
import com.mineinspect.app.data.local.dao.InspectionDao
import com.mineinspect.app.data.local.dao.MeasurementDao
import com.mineinspect.app.data.local.dao.ObservationDao
import com.mineinspect.app.data.local.entity.MineCacheEntity
import com.mineinspect.app.data.repository.MineRepository
import com.mineinspect.app.data.security.TokenStore
import com.mineinspect.app.sync.SyncMetadataWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val mines: List<MineCacheEntity> = emptyList(),
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val queuedItemCount: Int = 0
)

/** Replaces HomeScreen's hardcoded, identically-navigating 3-item mine queue (plan §22
 *  item 10), and its Sync tab's fake delay()-based "sync" button with a real aggregate
 *  queued count + real WorkManager trigger. */
@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val mineRepository: MineRepository,
    private val tokenStore: TokenStore,
    private val mineDao: com.mineinspect.app.data.local.dao.MineDao,
    private val sectionDefDao: com.mineinspect.app.data.local.dao.SectionDefDao,
    private val inspectionDao: InspectionDao,
    private val evidenceDao: EvidenceDao,
    private val observationDao: ObservationDao,
    private val measurementDao: MeasurementDao,
    private val gpsPointDao: GpsPointDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            seedDefaultMinesIfNeeded()
            mineRepository.observeMines().collect { mines ->
                _uiState.update { it.copy(mines = mines) }
            }
        }
        viewModelScope.launch {
            combine(
                inspectionDao.observeAllUnsyncedCount(),
                evidenceDao.observeAllUnsyncedCount(),
                observationDao.observeAllUnsyncedCount(),
                measurementDao.observeAllUnsyncedCount(),
                gpsPointDao.observeAllUnsyncedCount()
            ) { a, b, c, d, e -> a + b + c + d + e }
                .collect { total -> _uiState.update { it.copy(queuedItemCount = total) } }
        }
        refresh()
    }

    private suspend fun seedDefaultMinesIfNeeded() {
        if (mineDao.getById("dev-mine-1") == null) {
            val now = System.currentTimeMillis()
            mineDao.upsertAll(
                listOf(
                    MineCacheEntity(
                        mineId = "dev-mine-1",
                        name = "Mine 1",
                        permitNumber = "M-882",
                        hazardIndex = 78.0,
                        evidenceQuota = 3,
                        sectionCount = 3,
                        lastBriefingText = "Primary inspection audit section.",
                        cachedAt = now
                    ),
                    MineCacheEntity(
                        mineId = "dev-mine-2",
                        name = "Mine 1 (Dev)",
                        permitNumber = "M-882",
                        hazardIndex = 45.0,
                        evidenceQuota = 3,
                        sectionCount = 3,
                        lastBriefingText = "Development test mine.",
                        cachedAt = now
                    )
                )
            )
            sectionDefDao.upsertAll(
                listOf(
                    Triple(1, "Section 1 Main Deck", "Track gauge stability & emergency stop"),
                    Triple(2, "Section 2 Main Deck", "Feeders, transfer chutes & safety checks"),
                    Triple(3, "Section 3 Main Deck", "Perimeter lockouts & blast doors")
                ).flatMap { (index, label, description) ->
                    listOf(
                        com.mineinspect.app.data.local.entity.SectionDefEntity(
                            id = "dev-mine-1:$index",
                            mineId = "dev-mine-1",
                            sectionIndex = index,
                            label = label,
                            description = description,
                            evidenceQuota = 3
                        ),
                        com.mineinspect.app.data.local.entity.SectionDefEntity(
                            id = "dev-mine-2:$index",
                            mineId = "dev-mine-2",
                            sectionIndex = index,
                            label = label,
                            description = description,
                            evidenceQuota = 3
                        )
                    )
                }
            )
        }
    }

    fun refresh() {
        val inspectorId = tokenStore.getInspectorId() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            val result = mineRepository.refreshMines(inspectorId)
            _uiState.update {
                it.copy(
                    isRefreshing = false,
                    errorMessage = result.exceptionOrNull()
                        ?.let { e -> "Couldn't refresh mines: ${e.message ?: "network error"}" }
                )
            }
        }
    }

    fun syncNow() {
        viewModelScope.launch {
            // A row only reaches SYNC_FAILED after exhausting its automatic retries, so a
            // manual "Sync now" doubles as the user's way to give those another shot.
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
