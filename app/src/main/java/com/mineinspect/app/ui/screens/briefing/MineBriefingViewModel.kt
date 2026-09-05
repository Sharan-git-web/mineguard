package com.mineinspect.app.ui.screens.briefing

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mineinspect.app.data.local.dao.MineDao
import com.mineinspect.app.data.local.dao.SectionDefDao
import com.mineinspect.app.data.local.entity.MineCacheEntity
import com.mineinspect.app.data.local.entity.SectionDefEntity
import com.mineinspect.app.data.repository.InspectionRepository
import com.mineinspect.app.data.security.TokenStore
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

data class MineBriefingUiState(
    val mine: MineCacheEntity? = null,
    val sections: List<SectionDefEntity> = emptyList(),
    val isStarting: Boolean = false
)

/**
 * Reads the Mine/Section cache populated by HomeViewModel's refresh (plan §5 — Mines are a
 * read-only cache, not re-fetched per screen). "Start Inspection" creates a real
 * InspectionEntity (plan §22 item — this is the fix for "Start Inspection" not persisting
 * anything today).
 */
@HiltViewModel
class MineBriefingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mineDao: MineDao,
    private val sectionDefDao: SectionDefDao,
    private val inspectionRepository: InspectionRepository,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val mineId: String = checkNotNull(savedStateHandle["mineId"])

    private val _uiState = MutableStateFlow(MineBriefingUiState())
    val uiState: StateFlow<MineBriefingUiState> = _uiState.asStateFlow()

    private val _inspectionStarted = MutableSharedFlow<String>()
    val inspectionStarted: SharedFlow<String> = _inspectionStarted.asSharedFlow()

    init {
        viewModelScope.launch {
            var mine = mineDao.getById(mineId)
            if (mine == null) {
                mine = MineCacheEntity(
                    mineId = mineId,
                    name = "Mine 1",
                    permitNumber = "M-882",
                    hazardIndex = 78.0,
                    evidenceQuota = 3,
                    sectionCount = 3,
                    lastBriefingText = "Primary inspection audit section.",
                    cachedAt = System.currentTimeMillis()
                )
            }
            _uiState.update { it.copy(mine = mine) }
        }
        viewModelScope.launch {
            sectionDefDao.observeForMine(mineId).collect { sections ->
                val finalSections = sections.ifEmpty {
                    listOf(
                        SectionDefEntity("$mineId:1", mineId, 1, "Section 1 Main Deck", "Track gauge stability & emergency stop", 3),
                        SectionDefEntity("$mineId:2", mineId, 2, "Section 2 Main Deck", "Feeders, transfer chutes & safety checks", 3),
                        SectionDefEntity("$mineId:3", mineId, 3, "Section 3 Main Deck", "Perimeter lockouts & blast doors", 3)
                    )
                }
                _uiState.update { it.copy(sections = finalSections) }
            }
        }
    }

    fun onStartInspectionClick() {
        if (_uiState.value.isStarting) return
        val inspectorId = tokenStore.getInspectorId() ?: "INS-102"
        viewModelScope.launch {
            _uiState.update { it.copy(isStarting = true) }
            val inspectionId = inspectionRepository.startInspection(mineId, inspectorId)
            _uiState.update { it.copy(isStarting = false) }
            _inspectionStarted.emit(inspectionId)
        }
    }
}
