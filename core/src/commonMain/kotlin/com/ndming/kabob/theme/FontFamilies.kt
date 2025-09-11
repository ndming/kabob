package com.ndming.kabob.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.ndming.kabob.core.generated.resources.*
import com.ndming.kabob.core.generated.resources.Res
import org.jetbrains.compose.resources.Font

@Composable
fun getOpenSansFamily() = FontFamily(
    Font(Res.font.open_sans_extra_bold, FontWeight.ExtraBold),
    Font(Res.font.open_sans_extra_bold_italic, FontWeight.ExtraBold, FontStyle.Italic),
    Font(Res.font.open_sans_bold, FontWeight.Bold),
    Font(Res.font.open_sans_bold_italic, FontWeight.Bold, FontStyle.Italic),
    Font(Res.font.open_sans_semi_bold, FontWeight.SemiBold),
    Font(Res.font.open_sans_semi_bold_italic, FontWeight.SemiBold, FontStyle.Italic),
    Font(Res.font.open_sans_medium, FontWeight.Medium),
    Font(Res.font.open_sans_medium_italic, FontWeight.Medium, FontStyle.Italic),
    Font(Res.font.open_sans, FontWeight.Normal),
    Font(Res.font.open_sans_italic, FontWeight.Normal, FontStyle.Italic),
    Font(Res.font.open_sans_light, FontWeight.Light),
    Font(Res.font.open_sans_light_italic, FontWeight.Light, FontStyle.Italic),
)

@Composable
fun getJetBrainsMonoFamily() = FontFamily(
    Font(Res.font.jet_brains_mono_bold, FontWeight.Bold),
    Font(Res.font.jet_brains_mono_bold_italic, FontWeight.Bold, FontStyle.Italic),
    Font(Res.font.jet_brains_mono_semi_bold, FontWeight.SemiBold),
    Font(Res.font.jet_brains_mono_semi_bold_italic, FontWeight.SemiBold, FontStyle.Italic),
    Font(Res.font.jet_brains_mono_medium, FontWeight.Medium),
    Font(Res.font.jet_brains_mono_medium_italic, FontWeight.Medium, FontStyle.Italic),
    Font(Res.font.jet_brains_mono, FontWeight.Normal),
    Font(Res.font.jet_brains_mono_italic, FontWeight.Normal, FontStyle.Italic),
    Font(Res.font.jet_brains_mono_light, FontWeight.Light),
    Font(Res.font.jet_brains_mono_light_italic, FontWeight.Light, FontStyle.Italic),
)

@Suppress("SpellCheckingInspection")
@Composable
fun getNotoSansMathFamily() = FontFamily(
    Font(Res.font.noto_sans_math, FontWeight.Normal),
)
