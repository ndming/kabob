package com.ndming.kabob

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ndming.kabob.theme.KabobTheme
import com.ndming.kabob.theme.Profile
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
@JsName("mainGs2m")
fun main() {
    ComposeViewport(document.body!!) {
        val gs2mViewModel = viewModel { Gs2mViewModel() }
        val uiState by gs2mViewModel.uiState.collectAsState()

        KabobTheme(gs2mViewModel.currentProfile, gs2mViewModel.currentConcept) {
            LaunchedEffect(Unit) {
                gs2mViewModel.changeProfile(Profile.LIGHT)
            }

            Gs2mPage(
                uiState = uiState,
                onProfileChange = gs2mViewModel::changeProfile,
                onDtuViewerFrameRequest = gs2mViewModel::requestDtuViewerFrame,
                onDtuViewerSceneChange = gs2mViewModel::changeDtuViewerScene,
                onShinyViewerSceneChange = gs2mViewModel::changeShinyViewerScene,
                onShinyViewerPairedMapChange = gs2mViewModel::changeShinyViewerPairedMap,
                onShinyMeshSceneChange = gs2mViewModel::changeShinyMeshScene,
                onShinyMeshPairedMethodChange = gs2mViewModel::changeShinyMeshPairedMethod,
                onShinyViewerPlayingChange = gs2mViewModel::changeShinyViewerPlaying,
                onShinyMeshPlayingChange = gs2mViewModel::changeShinyMeshPlaying,
            )
        }
    }
}