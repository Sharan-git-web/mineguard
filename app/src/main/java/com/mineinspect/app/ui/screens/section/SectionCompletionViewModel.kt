package com.mineinspect.app.ui.screens.section

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mineinspect.app.data.local.dao.EvidenceDao
import com.mineinspect.app.data.local.dao.MeasurementDao
import com.mineinspect.app.data.local.dao.ObservationDao
import com.mineinspect.app.util.sectionIndexOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class SectionCompletionUiState(
    val photoCount: Int = 0,
    val observationCount: Int = 0,
    val measurementCount: Int = 0
)

/** Summary shown when a section is marked complete (backs SECTION_COMPLETION). */
@HiltViewModel
class SectionCompletionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    evidenceDao: EvidenceDao,
    observationDao: ObservationDao,
    measurementDao: MeasurementDao
) : ViewModel() {

    private val inspectionId: String = checkNotNull(savedStateHandle["inspectionId"])
    private val sectionIndex = sectionIndexOf(checkNotNull(savedStateHandle["sectionId"]))

    val uiState: StateFlow<SectionCompletionUiState> = combine(
        evidenceDao.observePhotoCount(inspectionId, sectionIndex),
        observationDao.observeForSection(inspectionId, sectionIndex).map { it.size },
        measurementDao.observeForSection(inspectionId, sectionIndex).map { it.size }
    ) { photos, observations, measurements ->
        SectionCompletionUiState(photos, observations, measurements)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SectionCompletionUiState())
}
