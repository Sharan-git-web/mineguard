package com.mineinspect.app.ui.screens.verification

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

data class WorkerVerificationUiState(
    val workerCountText: String = "",
    val allWearingPpe: Boolean = true,
    val notes: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

/** Backs the WORKER_VERIFICATION route. */
@HiltViewModel
class WorkerVerificationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observationRepository: ObservationRepository
) : ViewModel() {

    private val inspectionId: String = checkNotNull(savedStateHandle["inspectionId"])
    private val sectionIndex = sectionIndexOf(checkNotNull(savedStateHandle["sectionId"]))

    private val _uiState = MutableStateFlow(WorkerVerificationUiState())
    val uiState: StateFlow<WorkerVerificationUiState> = _uiState.asStateFlow()

    private val _saved = MutableSharedFlow<Unit>()
    val saved: SharedFlow<Unit> = _saved.asSharedFlow()

    fun onWorkerCountChange(value: String) {
        _uiState.update { it.copy(workerCountText = value, errorMessage = null) }
    }

    fun onToggleAllWearingPpe() {
        _uiState.update { it.copy(allWearingPpe = !it.allWearingPpe) }
    }

    fun onNotesChange(value: String) {
        _uiState.update { it.copy(notes = value) }
    }

    fun onSaveClick() {
        if (_uiState.value.isSaving) return
        val state = _uiState.value
        val count = state.workerCountText.toIntOrNull()
        if (count == null) {
            _uiState.update { it.copy(errorMessage = "Enter the number of workers verified present.") }
            return
        }

        val notes = buildString {
            append("Workers present: $count. ")
            append("All wearing required PPE: ${if (state.allWearingPpe) "Yes" else "No"}.")
            if (state.notes.isNotBlank()) append(" ${state.notes}")
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            observationRepository.addObservation(
                inspectionId = inspectionId,
                sectionIndex = sectionIndex,
                category = "WORKER_VERIFICATION",
                severity = (if (state.allWearingPpe) Severity.LOW else Severity.HIGH).name,
                notes = notes
            )
            _uiState.update { it.copy(isSaving = false) }
            _saved.emit(Unit)
        }
    }
}
