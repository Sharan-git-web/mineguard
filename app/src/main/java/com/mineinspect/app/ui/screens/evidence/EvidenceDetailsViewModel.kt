package com.mineinspect.app.ui.screens.evidence

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mineinspect.app.data.local.dao.EvidenceDao
import com.mineinspect.app.data.local.dao.GpsPointDao
import com.mineinspect.app.data.local.dao.InspectionDao
import com.mineinspect.app.data.local.dao.MineDao
import com.mineinspect.app.data.local.entity.EvidenceEntity
import com.mineinspect.app.data.local.entity.GpsPointEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EvidenceDetailsUiState(
    val evidence: EvidenceEntity? = null,
    val gpsPoint: GpsPointEntity? = null,
    val mineName: String? = null
)

/** Replaces EvidenceDetailsScreen's fabricated GPS/timestamp/hash metadata (plan §22 item 4). */
@HiltViewModel
class EvidenceDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val evidenceDao: EvidenceDao,
    private val gpsPointDao: GpsPointDao,
    private val inspectionDao: InspectionDao,
    private val mineDao: MineDao
) : ViewModel() {

    private val evidenceId: String = checkNotNull(savedStateHandle["evidenceId"])

    private val _uiState = MutableStateFlow(EvidenceDetailsUiState())
    val uiState: StateFlow<EvidenceDetailsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val evidence = evidenceDao.getById(evidenceId) ?: return@launch
            val gpsPoint = evidence.gpsPointId?.let { gpsPointDao.getById(it) }
            val inspection = inspectionDao.getById(evidence.inspectionId)
            val mine = inspection?.let { mineDao.getById(it.mineId) }
            _uiState.update { it.copy(evidence = evidence, gpsPoint = gpsPoint, mineName = mine?.name) }
        }
    }
}
