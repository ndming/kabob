package com.ndming.kabob

import androidx.lifecycle.ViewModel
import com.ndming.kabob.sitemap.KabobTopic
import com.ndming.kabob.theme.Concept
import com.ndming.kabob.theme.Profile
import kotlinx.browser.window
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class KabobUiState(
    val currentConcept: Concept,
    val currentProfile: Profile,
    val hideNavigation: Boolean,
)

class KabobViewModel(uiState: KabobUiState) : ViewModel() {
    private val _uiState = MutableStateFlow(uiState)
    val uiState: StateFlow<KabobUiState> = _uiState.asStateFlow()

    fun changeConcept(concept: Concept) {
        _uiState.update { it.copy(currentConcept = concept) }
    }

    fun changeProfile(profile: Profile) {
        _uiState.update { it.copy(currentProfile = profile) }
        window.sessionStorage.setItem(PROFILE_KEY, profile.name)
    }

    fun changeNavigationRailVisibility(hide: Boolean) {
        _uiState.update { it.copy(hideNavigation = hide) }
        window.sessionStorage.setItem(NAV_RAIL_KEY, hide.toString())
    }

    fun saveCurrentTopic(topic: KabobTopic) {
        window.sessionStorage.setItem(TOPIC_KEY, topic.route)
    }

    companion object {
        const val TOPIC_KEY = "topic"
        const val PROFILE_KEY = "profile"
        const val NAV_RAIL_KEY = "navRail"
    }
}