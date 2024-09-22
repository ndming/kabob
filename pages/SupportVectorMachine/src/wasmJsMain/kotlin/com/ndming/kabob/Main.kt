package com.ndming.kabob

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ndming.kabob.theme.KabobTheme
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        val svmViewModel = viewModel{ SupportVectorMachineViewModel() }

        KabobTheme(svmViewModel.currentProfile, svmViewModel.currentConcept) {
            SupportVectorMachinePage(
                onProfileChange = svmViewModel::changeProfile,
            )
        }
    }
}