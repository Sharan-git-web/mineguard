package com.mineinspect.app.ui.screens.section

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mineinspect.app.data.local.dao.EvidenceDao
import com.mineinspect.app.util.sectionIndexOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Derives photo count from real EvidenceEntity rows instead of the InspectionState
 *  singleton — this is what fixes the double-increment bug (plan §22 item 1). */
@HiltViewModel
class SectionMonitorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    evidenceDao: EvidenceDao
) : ViewModel() {

    private val inspectionId: String = checkNotNull(savedStateHandle["inspectionId"])
    private val sectionId: String = checkNotNull(savedStateHandle["sectionId"])
    private val sectionIndex = sectionIndexOf(sectionId)

    val photosCaptured: StateFlow<Int> = evidenceDao.observePhotoCount(inspectionId, sectionIndex)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
}
