package com.mineinspect.app.ui.screens.measurement

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mineinspect.app.data.repository.MeasurementRepository
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

data class MetricOption(val type: String, val label: String, val unit: String)

val MEASUREMENT_METRICS = listOf(
    MetricOption("GAS_LEVEL", "Gas Level", "ppm"),
    MetricOption("TEMPERATURE", "Temperature", "°C"),
    MetricOption("NOISE_DB", "Noise Level", "dB"),
    MetricOption("STRUCTURAL_GAP_MM", "Structural Gap", "mm")
)

data class MeasurementEntryUiState(
    val metric: MetricOption = MEASUREMENT_METRICS.first(),
    val valueText: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

/** Backs the MEASUREMENT_ENTRY route. Never computes pass/fail locally (plan §17) —
 *  thresholdStatus stays null until the server evaluates it on sync. */
@HiltViewModel
class MeasurementEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val measurementRepository: MeasurementRepository
) : ViewModel() {

    private val inspectionId: String = checkNotNull(savedStateHandle["inspectionId"])
    private val sectionIndex = sectionIndexOf(checkNotNull(savedStateHandle["sectionId"]))

    private val _uiState = MutableStateFlow(MeasurementEntryUiState())
    val uiState: StateFlow<MeasurementEntryUiState> = _uiState.asStateFlow()

    private val _saved = MutableSharedFlow<Unit>()
    val saved: SharedFlow<Unit> = _saved.asSharedFlow()

    fun onMetricChange(metric: MetricOption) {
        _uiState.update { it.copy(metric = metric, errorMessage = null) }
    }

    fun onValueChange(value: String) {
        _uiState.update { it.copy(valueText = value, errorMessage = null) }
    }

    fun onSaveClick() {
        if (_uiState.value.isSaving) return
        val state = _uiState.value
        val value = state.valueText.toDoubleOrNull()
        if (value == null) {
            _uiState.update { it.copy(errorMessage = "Enter a numeric value.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            measurementRepository.addMeasurement(
                inspectionId = inspectionId,
                sectionIndex = sectionIndex,
                metricType = state.metric.type,
                value = value,
                unit = state.metric.unit
            )
            _uiState.update { it.copy(isSaving = false) }
            _saved.emit(Unit)
        }
    }
}
