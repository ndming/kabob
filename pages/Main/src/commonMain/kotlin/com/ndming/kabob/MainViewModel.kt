package com.ndming.kabob

import com.ndming.kabob.theme.ThemeAwareViewModel
import kotlinx.browser.window
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MainUiState(
    val currentRoute: MainRoute,
    val hideNavigation: Boolean,
)

class MainViewModel : ThemeAwareViewModel() {
    private val _uiState: MutableStateFlow<MainUiState>

    val initialRoute: MainRoute = window.sessionStorage.getItem(MAIN_ROUTE_KEY)
        ?.let { value -> MainRoute.entries.find { it.route == value } } ?: MainRoute.Home

    init {
        val hideNavigation = window.sessionStorage.getItem(MAIN_NAV_RAIL_KEY)?.toBoolean() ?: true
        _uiState = MutableStateFlow(MainUiState(initialRoute, hideNavigation))
    }

    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun changeNavigationRailVisibility(hide: Boolean) {
        _uiState.update { it.copy(hideNavigation = hide) }
        window.sessionStorage.setItem(MAIN_NAV_RAIL_KEY, hide.toString())
    }

    fun changeRoute(route: MainRoute) {
        _uiState.update { it.copy(currentRoute = route) }
        window.sessionStorage.setItem(MAIN_ROUTE_KEY, route.route)
    }

    companion object {
        private const val MAIN_ROUTE_KEY = "main_route"
        private const val MAIN_NAV_RAIL_KEY = "main_nav_rail"
    }
}
