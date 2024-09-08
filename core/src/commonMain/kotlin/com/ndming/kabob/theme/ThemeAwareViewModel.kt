package com.ndming.kabob.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.browser.window
import org.w3c.dom.PageTransitionEvent

open class ThemeAwareViewModel : ViewModel() {
    var currentProfile: Profile by mutableStateOf(Profile.DARK)
        private set

    var currentConcept: Concept by mutableStateOf(Concept.CAPRICORN)
        private set

    init {
        initProfile()
        initConcept()

        window.addEventListener("pageshow") { event ->
            if ((event as PageTransitionEvent).persisted) {
                initProfile()
                initConcept()
            }
        }
    }

    private fun initProfile() {
        currentProfile = window.sessionStorage.getItem(CORE_PROFILE_KEY)
            ?.let { value -> Profile.entries.find { it.name == value } }
            ?: Profile.DARK
    }

    private fun initConcept() {
        currentConcept = window.sessionStorage.getItem(CORE_CONCEPT_KEY)
            ?. let { value -> Concept.entries.find { it.name == value } }
            ?: Concept.CAPRICORN
    }

    fun changeProfile(profile: Profile) {
        currentProfile = profile
        window.sessionStorage.setItem(CORE_PROFILE_KEY, profile.name)
    }

    fun changeConcept(concept: Concept) {
        currentConcept = concept
        window.sessionStorage.setItem(CORE_CONCEPT_KEY, concept.name)
    }

    companion object {
        private const val CORE_PROFILE_KEY = "core_theme_profile"
        private const val CORE_CONCEPT_KEY = "core_theme_concept"
    }
}