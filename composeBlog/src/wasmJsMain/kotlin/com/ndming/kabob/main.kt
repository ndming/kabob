package com.ndming.kabob

import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ndming.kabob.sitemap.KabobTopic
import com.ndming.kabob.theme.Concept
import com.ndming.kabob.theme.Profile
import kotlinx.browser.document
import kotlinx.browser.window

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val initialRoute = window.sessionStorage.getItem(KabobViewModel.TOPIC_KEY) ?: KabobTopic.HOME.route

    val profile = window.sessionStorage.getItem(KabobViewModel.PROFILE_KEY)
        ?.let { value -> Profile.entries.find { it.name == value } } ?: Profile.DARK

    val hideNavigation = window.sessionStorage.getItem(KabobViewModel.NAV_RAIL_KEY)?.toBoolean() ?: false

    ComposeViewport(document.body!!) {
        val kabobViewModel = viewModel { KabobViewModel(KabobUiState(Concept.CAPRICORN, profile, hideNavigation)) }
        val uiState by kabobViewModel.uiState.collectAsStateWithLifecycle()

        KabobApp(
            uiState = uiState,
            initialRoute = initialRoute,
            onProfileChange = kabobViewModel::changeProfile,
            onNavRailChange = kabobViewModel::changeNavigationRailVisibility,
            onTopicChange = kabobViewModel::saveCurrentTopic,
        )
    }
}