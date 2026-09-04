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

data class PpeItemState(val label: String, val passed: Boolean)

private val DEFAULT_PPE_ITEMS = listOf(
    PpeItemState("Helmet", true),
    PpeItemState("Reflective Vest", true),
    PpeItemState("Safety Boots", true),
    PpeItemState("Gloves", true),
    PpeItemState("Eye Protection", true)
)

data class PpeVerificationUiState(
    val items: List<PpeItemState> = DEFAULT_PPE_ITEMS,
    val isSaving: Boolean = false
)

/** Backs the PPE_VERIFICATION route — a manual inspector checklist, not on-device AI/CV
 *  detection (that's explicitly server-side only, plan §16). Compiles into one
 *  ObservationEntity summarizing the checklist. */
@HiltViewModel
class PpeVerificationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observationRepository: ObservationRepository
) : ViewModel() {

    private val inspectionId: String = checkNotNull(savedStateHandle["inspectionId"])
    private val sectionIndex = sectionIndexOf(checkNotNull(savedStateHandle["sectionId"]))

    private val _uiState = MutableStateFlow(PpeVerificationUiState())
    val uiState: StateFlow<PpeVerificationUiState> = _uiState.asStateFlow()

    private val _saved = MutableSharedFlow<Unit>()
    val saved: SharedFlow<Unit> = _saved.asSharedFlow()

    fun onToggleItem(index: Int) {
        _uiState.update { state ->
            state.copy(
                items = state.items.mapIndexed { i, item ->
                    if (i == index) item.copy(passed = !item.passed) else item
                }
            )
        }
    }

    fun onSaveClick() {
        if (_uiState.value.isSaving) return
        val items = _uiState.value.items
        val anyFailed = items.any { !it.passed }
        val notes = items.joinToString("; ") { "${it.label}: ${if (it.passed) "Pass" else "Fail"}" }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            observationRepository.addObservation(
                inspectionId = inspectionId,
                sectionIndex = sectionIndex,
                category = "PPE_VERIFICATION",
                severity = (if (anyFailed) Severity.CRITICAL else Severity.LOW).name,
                notes = notes
            )
            _uiState.update { it.copy(isSaving = false) }
            _saved.emit(Unit)
        }
    }
}
