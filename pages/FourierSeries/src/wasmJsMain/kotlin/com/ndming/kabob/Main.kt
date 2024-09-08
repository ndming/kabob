package com.ndming.kabob

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ndming.kabob.theme.KabobTheme
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
@JsName("mainFourierSeries")
fun main() {
    ComposeViewport(document.body!!) {
        val fsViewModel = viewModel{ FourierSeriesViewModel() }
        val uiState by fsViewModel.uiState.collectAsState()

        KabobTheme(fsViewModel.currentProfile, fsViewModel.currentConcept) {
            FourierSeriesPage(
                uiState = uiState,
                currentTime = fsViewModel.currentTime,
                arrowStates = fsViewModel.arrowsStates.take(uiState.arrowCount),
                onProfileChange = fsViewModel::changeProfile,
                onTimeChange = fsViewModel::changeTime,
                onPlay = fsViewModel::play,
                onPause = fsViewModel::pause,
                onDurationScaleChange = fsViewModel::changeDurationScale,
                onAddArrow = fsViewModel::addArrow,
                onDropArrow = fsViewModel::dropArrow,
                onLockToPathChange = fsViewModel::changeLockToPath,
                onDrawableChange = fsViewModel::changeDrawable,
                onFadingFactorChange = fsViewModel::changeFadingFactor,
                onResetFadingFactor = fsViewModel::resetFadingFactor,
                onZoomFactor = fsViewModel::changeZoomFactor,
                onSamplingRate = fsViewModel::changeSamplingRate,
            )
        }
    }
}