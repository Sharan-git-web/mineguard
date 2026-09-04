package com.mineinspect.app.ui.screens.tracking

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mineinspect.app.data.local.dao.GpsPointDao
import com.mineinspect.app.data.local.entity.GpsPointSource
import com.mineinspect.app.data.location.LocationRepository
import com.mineinspect.app.data.repository.GpsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ActiveTrackingUiState(
    val isTracking: Boolean = false,
    val pointsLogged: Int = 0,
    val lastAccuracyMeters: Float? = null,
    val markerLogged: Boolean = false
)

/** Real breadcrumb GPS tracking + hazard-marker drop (plan §7, fixes plan §22 items 6-7 —
 *  the previously empty placeholder trail and the no-op marker button). */
@HiltViewModel
class ActiveTrackingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val appContext: Context,
    private val locationRepository: LocationRepository,
    private val gpsRepository: GpsRepository,
    gpsPointDao: GpsPointDao
) : ViewModel() {

    private val inspectionId: String = checkNotNull(savedStateHandle["inspectionId"])

    private val _uiState = MutableStateFlow(ActiveTrackingUiState())
    val uiState: StateFlow<ActiveTrackingUiState> = _uiState.asStateFlow()

    val pointCount: StateFlow<Int> = gpsPointDao.observeCountForInspection(inspectionId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun startTrackingIfPermitted() {
        val hasPermission = ContextCompat.checkSelfPermission(
            appContext, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasPermission || _uiState.value.isTracking) return

        _uiState.update { it.copy(isTracking = true) }
        viewModelScope.launch {
            locationRepository.observeLocationUpdates(BREADCRUMB_INTERVAL_MILLIS).collect { fix ->
                gpsRepository.recordFix(inspectionId, sectionIndex = null, fix = fix, source = GpsPointSource.BREADCRUMB)
                _uiState.update { it.copy(lastAccuracyMeters = fix.accuracyMeters) }
            }
        }
    }

    fun onDropHazardMarker() {
        viewModelScope.launch {
            val fix = locationRepository.getCurrentLocation()
            if (fix != null) {
                gpsRepository.recordFix(inspectionId, sectionIndex = null, fix = fix, source = GpsPointSource.HAZARD_MARKER)
            }
            _uiState.update { it.copy(markerLogged = true) }
        }
    }

    fun onMarkerFlashConsumed() {
        _uiState.update { it.copy(markerLogged = false) }
    }

    private companion object {
        const val BREADCRUMB_INTERVAL_MILLIS = 20_000L
    }
}
