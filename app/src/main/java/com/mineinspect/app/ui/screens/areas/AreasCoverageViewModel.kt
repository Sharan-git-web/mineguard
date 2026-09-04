package com.mineinspect.app.ui.screens.areas

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mineinspect.app.data.local.dao.EvidenceDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class AreasCoverageUiState(val sec2Photos: Int = 0, val sec3Photos: Int = 0)

/** Replaces InspectionState.getPhotoCount() reads with real Room-derived counts (plan §22 item 1). */
@HiltViewModel
class AreasCoverageViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    evidenceDao: EvidenceDao
) : ViewModel() {

    private val inspectionId: String = checkNotNull(savedStateHandle["inspectionId"])

    val uiState: StateFlow<AreasCoverageUiState> = combine(
        evidenceDao.observePhotoCount(inspectionId, 2),
        evidenceDao.observePhotoCount(inspectionId, 3)
    ) { sec2, sec3 -> AreasCoverageUiState(sec2, sec3) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AreasCoverageUiState())
}
