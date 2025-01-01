package com.ndming.kabob

import com.ndming.kabob.theme.ThemeAwareViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PendulumUiState(
    val swinging: Boolean = false,
)

class PendulumViewModel : ThemeAwareViewModel() {
    private val _uiState = MutableStateFlow(PendulumUiState())
    val uiState: StateFlow<PendulumUiState> = _uiState.asStateFlow()
}