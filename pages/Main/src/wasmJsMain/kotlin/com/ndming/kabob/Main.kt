package com.ndming.kabob

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ndming.kabob.theme.KabobTheme
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
@JsName("mainPage")
fun main() {
    ComposeViewport(document.body!!) {
        val mainViewModel = viewModel { MainViewModel() }
        val uiState by mainViewModel.uiState.collectAsState()

        KabobTheme(mainViewModel.currentProfile, mainViewModel.currentConcept) {
            MainPage(
                uiState = uiState,
                initialRoute = mainViewModel.initialRoute.route,
                onRouteChange = mainViewModel::changeRoute,
                onNavRailVisible = mainViewModel::changeNavigationRailVisibility,
                onProfileChange = mainViewModel::changeProfile,
            )
        }
    }
}