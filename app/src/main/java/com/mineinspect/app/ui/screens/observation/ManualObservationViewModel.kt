package com.mineinspect.app.ui.screens.observation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mineinspect.app.data.repository.ObservationRepository
import com.mineinspect.app.ui.components.Severity
import com.mineinspect.app.util.sectionIndexOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ManualObservationUiState(
    val category: String = CATEGORIES.first(),
    val severity: Severity = Severity.LOW,
    val notes: String = "",
    val isSaving: Boolean = false
) {
    companion object {
        val CATEGORIES = listOf("GENERAL", "PPE", "STRUCTURAL", "ANOMALY", "MAINTENANCE")
    }
}

/** Backs the MANUAL_OBSERVATION route; also covers ANOMALY_WARNING's intent via the
 *  "ANOMALY" category rather than a separate screen — see plan status table, which
 *  already groups those two routes together. */
@HiltViewModel
class ManualObservationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observationRepository: ObservationRepository
) : ViewModel() {

    private val inspectionId: String = checkNotNull(savedStateHandle["inspectionId"])
    private val sectionIndex = sectionIndexOf(checkNotNull(savedStateHandle["sectionId"]))

    private val _uiState = MutableStateFlow(ManualObservationUiState())
    val uiState: StateFlow<ManualObservationUiState> = _uiState.asStateFlow()

    private val _saved = MutableSharedFlow<Unit>()
    val saved: SharedFlow<Unit> = _saved.asSharedFlow()

    fun onCategoryChange(value: String) {
        _uiState.update { it.copy(category = value) }
    }

    fun onSeverityChange(value: Severity) {
        _uiState.update { it.copy(severity = value) }
    }

    fun onNotesChange(value: String) {
        _uiState.update { it.copy(notes = value) }
    }

    fun onSaveClick() {
        if (_uiState.value.isSaving) return
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            observationRepository.addObservation(
                inspectionId = inspectionId,
                sectionIndex = sectionIndex,
                category = state.category,
                severity = state.severity.name,
                notes = state.notes
            )
            _uiState.update { it.copy(isSaving = false) }
            _saved.emit(Unit)
        }
    }
}
