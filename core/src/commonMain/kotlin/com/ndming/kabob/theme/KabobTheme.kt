package com.ndming.kabob.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.font.FontFamily

@Composable
fun KabobTheme(
    profile: Profile,
    concept: Concept,
    content: @Composable () -> Unit
) {
    val colorScheme = if (profile == Profile.LIGHT) concept.lightScheme else concept.darkScheme
    val fontFamily = getOpenSansFamily()

    CompositionLocalProvider(LocalKabobTheme provides KabobTheme(profile, concept)) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = getKabobTypography(fontFamily),
            content = content
        )
    }
}

val LocalKabobTheme = staticCompositionLocalOf{ KabobTheme() }

@Immutable
data class KabobTheme(
    val profile: Profile = Profile.DARK,
    val concept: Concept = Concept.CAPRICORN,
)

enum class Concept(val lightScheme: ColorScheme, val darkScheme: ColorScheme) {
    CAPRICORN(capricornLightScheme, capricornDarkScheme),
}

enum class Profile {
    LIGHT,
    DARK,
}

@Composable
private fun getKabobTypography(family: FontFamily) = Typography(
    displayLarge = typography.displayLarge.copy(fontFamily = family),
    displayMedium = typography.displayMedium.copy(fontFamily = family),
    displaySmall = typography.displaySmall.copy(fontFamily = family),
    headlineLarge = typography.headlineLarge.copy(fontFamily = family),
    headlineMedium = typography.headlineMedium.copy(fontFamily = family),
    headlineSmall = typography.headlineSmall.copy(fontFamily = family),
    titleLarge = typography.titleLarge.copy(fontFamily = family),
    titleMedium = typography.titleMedium.copy(fontFamily = family),
    titleSmall = typography.titleSmall.copy(fontFamily = family),
    bodyLarge = typography.bodyLarge.copy(fontFamily = family),
    bodyMedium = typography.bodyMedium.copy(fontFamily = family),
    bodySmall = typography.bodySmall.copy(fontFamily = family),
    labelLarge = typography.labelLarge.copy(fontFamily = family),
    labelMedium = typography.labelMedium.copy(fontFamily = family),
    labelSmall = typography.labelSmall.copy(fontFamily = family),
)
