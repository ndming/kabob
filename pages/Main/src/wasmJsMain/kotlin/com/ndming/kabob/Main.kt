package com.ndming.kabob

import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ndming.kabob.theme.*
import com.ndming.kabob.theme.KabobTheme
import kotlinx.browser.document
import kotlinx.browser.window

@OptIn(ExperimentalComposeUiApi::class)
@JsName("mainPage")
fun main() {
    val initialUiState = window.getInitialMainUiState()

    ComposeViewport(document.body!!) {
        val mainViewModel = viewModel { MainViewModel(initialUiState) }
        val uiState by mainViewModel.uiState.collectAsState()

        KabobTheme(uiState.currentProfile, uiState.currentConcept) {
            MainPage(
                uiState = uiState,
                initialRoute = initialUiState.currentRoute.route,
                onRouteChange = mainViewModel::changeRoute,
                onNavRailVisible = mainViewModel::changeNavigationRailVisibility,
                currentProfile = uiState.currentProfile,
                onProfileChange = mainViewModel::changeProfile,
            )
        }
    }
}