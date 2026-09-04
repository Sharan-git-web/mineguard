package com.mineinspect.app.ui.screens.map

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mineinspect.app.data.local.entity.GpsPointSource
import com.mineinspect.app.data.location.LocationRepository
import com.mineinspect.app.data.repository.GpsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RouteMapUiState(
    val hasInspectionContext: Boolean,
    val isDroppingPin: Boolean = false,
    val pinDropped: Boolean = false
)

/** Fixes RouteMapScreen's no-op hazard-pin button (plan §22 item 6). Reachable both from
 *  Home (no active inspection) and ActiveTracking (has one) — the pin button is only
 *  enabled when there's an inspection to attach the marker to. */
@HiltViewModel
class RouteMapViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val locationRepository: LocationRepository,
    private val gpsRepository: GpsRepository
) : ViewModel() {

    private val inspectionId: String? = savedStateHandle["inspectionId"]

    private val _uiState = MutableStateFlow(RouteMapUiState(hasInspectionContext = inspectionId != null))
    val uiState: StateFlow<RouteMapUiState> = _uiState.asStateFlow()

    fun onDropHazardPin() {
        val id = inspectionId ?: return
        if (_uiState.value.isDroppingPin) return
        viewModelScope.launch {
            _uiState.update { it.copy(isDroppingPin = true) }
            val fix = locationRepository.getCurrentLocation()
            if (fix != null) {
                gpsRepository.recordFix(id, sectionIndex = null, fix = fix, source = GpsPointSource.HAZARD_MARKER)
            }
            _uiState.update { it.copy(isDroppingPin = false, pinDropped = fix != null) }
        }
    }
}
