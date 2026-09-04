package com.mineinspect.app.ui.screens.gpsgate

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mineinspect.app.data.local.dao.InspectionDao
import com.mineinspect.app.data.local.dao.MineDao
import com.mineinspect.app.data.local.entity.GpsPointSource
import com.mineinspect.app.data.location.LocationRepository
import com.mineinspect.app.data.repository.GpsRepository
import com.mineinspect.app.data.security.TokenStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GpsGateUiState(
    val inspectorId: String? = null,
    val mineName: String? = null,
    val hasFix: Boolean = false,
    val accuracyMeters: Float? = null,
    val capturedAt: Long? = null,
    val isAcquiring: Boolean = false,
    val permissionDenied: Boolean = false
)

/**
 * Replaces GpsGateScreen's fabricated GNSS telemetry (plan §7, §22 item 3). The gate's
 * pass condition here is "a real fix was acquired" — the accuracy *threshold* that would
 * make a fix acceptable is explicitly a backend decision (plan §17), not something this
 * ViewModel hardcodes.
 */
@HiltViewModel
class GpsGateViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val locationRepository: LocationRepository,
    private val gpsRepository: GpsRepository,
    private val inspectionDao: InspectionDao,
    private val mineDao: MineDao,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val inspectionId: String = checkNotNull(savedStateHandle["inspectionId"])

    private val _uiState = MutableStateFlow(GpsGateUiState(inspectorId = tokenStore.getInspectorId()))
    val uiState: StateFlow<GpsGateUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val inspection = inspectionDao.getById(inspectionId)
            val mine = inspection?.let { mineDao.getById(it.mineId) }
            _uiState.update { it.copy(mineName = mine?.name) }
        }
    }

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
                _uiState.update {
                    it.copy(
                        hasFix = true,
                        accuracyMeters = fix.accuracyMeters,
                        capturedAt = fix.capturedAt,
                        isAcquiring = false
                    )
                }
            } else {
                _uiState.update { it.copy(isAcquiring = false) }
            }
        }
    }
}
