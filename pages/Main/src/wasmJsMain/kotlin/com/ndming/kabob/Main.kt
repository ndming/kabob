package com.ndming.kabob

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ndming.kabob.theme.Concept
import com.ndming.kabob.theme.KabobTheme
import com.ndming.kabob.theme.LocalKabobTheme
import com.ndming.kabob.theme.Profile
import kotlinx.browser.document
import kotlinx.browser.window

@OptIn(ExperimentalComposeUiApi::class)
@JsName("mainPage")
fun main() {
    val initialRoute = window.sessionStorage.getItem(MainViewModel.ROUTE_KEY)
        ?.let { value -> MainRoute.entries.find { it.route == value } } ?: MainRoute.Home

    val profile = window.sessionStorage.getItem(MainViewModel.PROFILE_KEY)
        ?.let { value -> Profile.entries.find { it.name == value } } ?: Profile.DARK

    val hideNavigation = window.sessionStorage.getItem(MainViewModel.NAV_RAIL_KEY)?.toBoolean() ?: true

    val initialUiState = MainUiState(profile, Concept.CAPRICORN, initialRoute, hideNavigation)

    ComposeViewport(document.body!!) {
        val mainViewModel = viewModel { MainViewModel(initialUiState) }

        val kabobTheme = KabobTheme(initialUiState.currentProfile, initialUiState.currentConcept)

        val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

        CompositionLocalProvider(LocalKabobTheme provides kabobTheme) {
            KabobTheme {
                MainPage(
                    uiState = uiState,
                    initialRoute = initialRoute.route,
                    onProfileChange = mainViewModel::changeProfile,
                    onRouteChange = mainViewModel::changeRoute,
                    onNavRailVisible = mainViewModel::changeNavigationRailVisibility,
                )
            }
        }
    }
}