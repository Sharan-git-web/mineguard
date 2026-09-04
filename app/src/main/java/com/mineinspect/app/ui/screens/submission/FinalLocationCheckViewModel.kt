package com.mineinspect.app.ui.screens.submission

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

data class FinalLocationCheckUiState(
    val hasFix: Boolean = false,
    val accuracyMeters: Float? = null,
    val isAcquiring: Boolean = false,
    val permissionDenied: Boolean = false
)

/** Backs FINAL_LOCATION_CHECK — a final real GPS fix before allowing submission,
 *  reusing the same LocationRepository as the initial GPS gate (plan §7). */
@HiltViewModel
class FinalLocationCheckViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val locationRepository: LocationRepository,
    private val gpsRepository: GpsRepository
) : ViewModel() {

    private val inspectionId: String = checkNotNull(savedStateHandle["inspectionId"])

    private val _uiState = MutableStateFlow(FinalLocationCheckUiState())
    val uiState: StateFlow<FinalLocationCheckUiState> = _uiState.asStateFlow()

    fun onPermissionResult(granted: Boolean) {
        if (granted) acquireFix() else _uiState.update { it.copy(permissionDenied = true) }
    }

    fun acquireFix() {
        if (_uiState.value.isAcquiring) return
        viewModelScope.launch {
            _uiState.update { it.copy(isAcquiring = true, permissionDenied = false) }
            val fix = locationRepository.getCurrentLocation()
            if (fix != null) {
                gpsRepository.recordFix(inspectionId, sectionIndex = null, fix = fix, source = GpsPointSource.GPS_GATE)
            }
            _uiState.update { it.copy(hasFix = fix != null, accuracyMeters = fix?.accuracyMeters, isAcquiring = false) }
        }
    }
}
