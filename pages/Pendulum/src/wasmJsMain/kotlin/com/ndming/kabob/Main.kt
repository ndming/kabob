package com.ndming.kabob

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ndming.kabob.theme.KabobTheme
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
@JsName("mainPendulum")
fun main() {
    ComposeViewport(document.body!!) {
        val pendulumViewModel = viewModel { PendulumViewModel() }
        val uiState by pendulumViewModel.uiState.collectAsState()

        KabobTheme(pendulumViewModel.currentProfile, pendulumViewModel.currentConcept) {
            PendulumPage(
                uiState = uiState,
                onProfileChange = pendulumViewModel::changeProfile,
            )
        }
    }
}