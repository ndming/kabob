package com.ndming.kabob

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ndming.kabob.theme.KabobTheme
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
@JsName("mainMetaballs")
fun main() {
    ComposeViewport(document.body!!) {
        val metaballsViewModel = viewModel { MetaballsViewModel() }
        val uiState by metaballsViewModel.uiState.collectAsState()

        KabobTheme(metaballsViewModel.currentProfile, metaballsViewModel.currentConcept) {
            MetaballsPage(
                uiState = uiState,
                onProfileChange = metaballsViewModel::changeProfile,
            )
        }

    }
}