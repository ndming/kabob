package com.ndming.kabob

import com.ndming.kabob.theme.ThemeAwareViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Gs2mUiState(
    val currentAnimatingScan: Int = 24,
)

class Gs2mViewModel : ThemeAwareViewModel() {
    private val _uiState = MutableStateFlow(Gs2mUiState())
    val uiState: StateFlow<Gs2mUiState> = _uiState.asStateFlow()
}