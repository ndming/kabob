package com.ndming.kabob

import com.ndming.kabob.theme.ThemeAwareViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MetaballsUiState(
    val playing: Boolean = false,
)

class MetaballsViewModel : ThemeAwareViewModel() {
    private val _uiState = MutableStateFlow(MetaballsUiState())
    val uiState: StateFlow<MetaballsUiState> = _uiState.asStateFlow()
}