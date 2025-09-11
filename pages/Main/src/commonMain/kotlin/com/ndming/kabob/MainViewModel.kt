package com.ndming.kabob

import com.ndming.kabob.theme.ThemeAwareViewModel
import kotlinx.browser.window
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MainUiState(
    val currentRoute: MainTopic,
    val hideNavPanel: Boolean,
)

class MainViewModel : ThemeAwareViewModel() {
    private val _uiState: MutableStateFlow<MainUiState>

    val initialRoute: MainTopic = window.sessionStorage.getItem(MAIN_ROUTE_KEY)
        ?.let { value -> MainTopic.entries.find { it.route == value } } ?: MainTopic.Home

    init {
        val hideNavigation = window.sessionStorage.getItem(MAIN_NAV_VISIBLE_KEY)?.toBoolean() ?: false
        _uiState = MutableStateFlow(MainUiState(initialRoute, hideNavigation))
    }

    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun changeNavPanelVisibility(hide: Boolean) {
        _uiState.update { it.copy(hideNavPanel = hide) }
        window.sessionStorage.setItem(MAIN_NAV_VISIBLE_KEY, hide.toString())
    }

    fun changeRoute(route: MainTopic) {
        _uiState.update { it.copy(currentRoute = route) }
        window.sessionStorage.setItem(MAIN_ROUTE_KEY, route.route)
    }

    companion object {
        private const val MAIN_ROUTE_KEY = "main_route"
        private const val MAIN_NAV_VISIBLE_KEY = "main_nav_panel_visible"
    }
}
