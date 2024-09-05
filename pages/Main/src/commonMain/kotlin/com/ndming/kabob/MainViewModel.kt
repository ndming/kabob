package com.ndming.kabob

import androidx.lifecycle.ViewModel
import com.ndming.kabob.theme.Concept
import com.ndming.kabob.theme.Profile
import kotlinx.browser.window
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MainUiState(
    val currentProfile: Profile,
    val currentConcept: Concept,
    val currentRoute: MainRoute,
    val hideNavigation: Boolean,
)

class MainViewModel(uiState: MainUiState) : ViewModel() {
    private val _uiState = MutableStateFlow(uiState)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun changeProfile(profile: Profile) {
        _uiState.update { it.copy(currentProfile = profile) }
        window.sessionStorage.setItem(PROFILE_KEY, profile.name)
    }

    fun changeNavigationRailVisibility(hide: Boolean) {
        _uiState.update { it.copy(hideNavigation = hide) }
        window.sessionStorage.setItem(NAV_RAIL_KEY, hide.toString())
    }

    fun changeRoute(route: MainRoute) {
        _uiState.update { it.copy(currentRoute = route) }
        window.sessionStorage.setItem(ROUTE_KEY, route.route)
    }

    companion object {
        const val ROUTE_KEY = "main_route"
        const val PROFILE_KEY = "profile"
        const val NAV_RAIL_KEY = "nav_rail"
    }
}
