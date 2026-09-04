package com.mineinspect.app.ui.screens.submission

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mineinspect.app.data.local.SyncState
import com.mineinspect.app.data.local.dao.EvidenceDao
import com.mineinspect.app.data.local.dao.GpsPointDao
import com.mineinspect.app.data.local.dao.InspectionDao
import com.mineinspect.app.data.local.dao.MeasurementDao
import com.mineinspect.app.data.local.dao.ObservationDao
import com.mineinspect.app.data.local.entity.InspectionStatus
import com.mineinspect.app.data.remote.InspectionApi
import com.mineinspect.app.data.remote.dto.SubmitRequestDto
import com.mineinspect.app.report.ReportGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class FinalReviewUiState(
    val photoCount: Int = 0,
    val observationCount: Int = 0,
    val measurementCount: Int = 0,
    val gpsPointCount: Int = 0,
    val unsyncedCount: Int = 0,
    val isSubmitting: Boolean = false,
    val isGeneratingReport: Boolean = false,
    val errorMessage: String? = null
)

/** Backs FINAL_REVIEW (and covers INSPECTION_SUMMARY's intent in the same screen — see
 *  plan §20 phase notes). Submission is blocked client-side while any child row is still
 *  unsynced (plan §12-13 #13, §14) rather than sent and rejected. */
@HiltViewModel
class FinalReviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val inspectionDao: InspectionDao,
    private val evidenceDao: EvidenceDao,
    private val observationDao: ObservationDao,
    private val measurementDao: MeasurementDao,
    private val gpsPointDao: GpsPointDao,
    private val inspectionApi: InspectionApi,
    private val reportGenerator: ReportGenerator
) : ViewModel() {

    private val inspectionId: String = checkNotNull(savedStateHandle["inspectionId"])

    private val _uiState = MutableStateFlow(FinalReviewUiState())
    val uiState: StateFlow<FinalReviewUiState> = _uiState.asStateFlow()

    private val _submitted = MutableSharedFlow<Unit>()
    val submitted: SharedFlow<Unit> = _submitted.asSharedFlow()

    private val _reportReady = MutableSharedFlow<File>()
    val reportReady: SharedFlow<File> = _reportReady.asSharedFlow()

    init {
        viewModelScope.launch {
            combine(
                evidenceDao.observeCountForInspection(inspectionId),
                observationDao.observeCountForInspection(inspectionId),
                measurementDao.observeCountForInspection(inspectionId),
                gpsPointDao.observeCountForInspection(inspectionId)
            ) { photos, observations, measurements, gps -> listOf(photos, observations, measurements, gps) }
                .collect { c ->
                    _uiState.update {
                        it.copy(photoCount = c[0], observationCount = c[1], measurementCount = c[2], gpsPointCount = c[3])
                    }
                }
        }
        viewModelScope.launch {
            combine(
                evidenceDao.observeUnsyncedCount(inspectionId),
                observationDao.observeUnsyncedCount(inspectionId),
                measurementDao.observeUnsyncedCount(inspectionId),
                gpsPointDao.observeUnsyncedCount(inspectionId)
            ) { a, b, c, d -> a + b + c + d }
                .collect { total -> _uiState.update { it.copy(unsyncedCount = total) } }
        }
    }

    fun onGenerateReportClick() {
        if (_uiState.value.isGeneratingReport) return
        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingReport = true, errorMessage = null) }
            try {
                val file = reportGenerator.generate(inspectionId)
                _reportReady.emit(file)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Report generation failed: ${e.message ?: "unknown error"}") }
            } finally {
                _uiState.update { it.copy(isGeneratingReport = false) }
            }
        }
    }

    fun onSubmitClick() {
        if (_uiState.value.isSubmitting) return
        val unsynced = _uiState.value.unsyncedCount
        if (unsynced > 0) {
            _uiState.update { it.copy(errorMessage = "$unsynced item(s) still syncing — wait for sync to finish before submitting.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            try {
                inspectionApi.submit(inspectionId, SubmitRequestDto())
                val now = System.currentTimeMillis()
                inspectionDao.markSubmitted(inspectionId, InspectionStatus.SUBMITTED.name, SyncState.PROCESSING.name, now, now)
                _uiState.update { it.copy(isSubmitting = false) }
                _submitted.emit(Unit)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSubmitting = false, errorMessage = "Submit failed: ${e.message ?: "network error"}. Your data is safe locally — try again once connected.")
                }
            }
        }
    }
}
