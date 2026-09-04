package com.mineinspect.app.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mineinspect.app.BuildConfig
import com.mineinspect.app.data.local.dao.MineDao
import com.mineinspect.app.data.local.dao.SectionDefDao
import com.mineinspect.app.data.local.entity.MineCacheEntity
import com.mineinspect.app.data.local.entity.SectionDefEntity
import com.mineinspect.app.data.repository.AuthRepository
import com.mineinspect.app.data.repository.AuthResult
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

data class LoginUiState(
    val inspectorId: String = "",
    val pin: String = "",
    val pinVisible: Boolean = false,
    val isSigningIn: Boolean = false,
    val errorMessage: String? = null
)

/** Replaces LoginScreen's hardcoded unconditional sign-in (plan §22 item 9). */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenStore: TokenStore,
    private val mineDao: MineDao,
    private val sectionDefDao: SectionDefDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _signedIn = MutableSharedFlow<Unit>()
    val signedIn: SharedFlow<Unit> = _signedIn.asSharedFlow()

    fun onInspectorIdChange(value: String) {
        _uiState.update { it.copy(inspectorId = value, errorMessage = null) }
    }

    fun onPinChange(value: String) {
        _uiState.update { it.copy(pin = value, errorMessage = null) }
    }

    fun onTogglePinVisibility() {
        _uiState.update { it.copy(pinVisible = !it.pinVisible) }
    }

    fun onSignInClick() {
        val state = _uiState.value
        if (state.isSigningIn) return
        if (state.inspectorId.isBlank() || state.pin.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Enter your Inspector ID and PIN.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSigningIn = true, errorMessage = null) }
            when (val result = authRepository.login(state.inspectorId, state.pin)) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isSigningIn = false) }
                    _signedIn.emit(Unit)
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isSigningIn = false, errorMessage = result.message) }
                }
            }
        }
    }

    /**
     * Debug-build-only escape hatch for testing the app without a reachable backend.
     * Creates a real local session for whatever Inspector ID was typed (falls back to
     * "INS-102" if blank) and seeds one local mine + its sections directly into Room, so
     * Home/Briefing/GpsGate/Sections/Evidence are all navigable — no network involved.
     * Inspections created from here still queue for sync exactly like a real session
     * (and will genuinely sync once a backend is reachable); this only bypasses the
     * *login* network call, nothing downstream is faked at the architecture level.
     */
    fun onDevBypassClick() {
        if (!BuildConfig.DEBUG) return
        if (_uiState.value.isSigningIn) return
        val id = _uiState.value.inspectorId.ifBlank { "INS-102" }

        viewModelScope.launch {
            _uiState.update { it.copy(isSigningIn = true, errorMessage = null) }
            tokenStore.saveSession(
                accessToken = "dev-bypass-token",
                refreshToken = "dev-bypass-refresh",
                expiresAtEpochMillis = Long.MAX_VALUE,
                inspectorId = id,
                inspectorName = "Dev Tester ($id)"
            )
            seedDevMineIfNeeded()
            _uiState.update { it.copy(isSigningIn = false) }
            _signedIn.emit(Unit)
        }
    }

    private suspend fun seedDevMineIfNeeded() {
        if (mineDao.getById(DEV_MINE_ID) != null) return
        val now = System.currentTimeMillis()
        mineDao.upsertAll(
            listOf(
                MineCacheEntity(
                    mineId = DEV_MINE_ID,
                    name = "Mine 1 (Dev)",
                    permitNumber = "M-882",
                    hazardIndex = 78.0,
                    evidenceQuota = 3,
                    sectionCount = 3,
                    lastBriefingText = "Local dev-seeded mine — no backend required to reach this screen.",
                    cachedAt = now
                )
            )
        )
        sectionDefDao.upsertAll(
            listOf(
                Triple(1, "Section 1 Main Deck", "Track gauge stability & emergency stop"),
                Triple(2, "Section 2 Main Deck", "Feeders, transfer chutes & safety checks"),
                Triple(3, "Section 3 Main Deck", "Perimeter lockouts & blast doors")
            ).map { (index, label, description) ->
                SectionDefEntity(
                    id = "$DEV_MINE_ID:$index",
                    mineId = DEV_MINE_ID,
                    sectionIndex = index,
                    label = label,
                    description = description,
                    evidenceQuota = 3
                )
            }
        )
    }

    private companion object {
        const val DEV_MINE_ID = "dev-mine-1"
    }
}
