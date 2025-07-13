package com.ndming.kabob.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

enum class AdaptiveSize {
    Wide,
    Landscape,
    Portrait,
    Thin,
}

class AdaptiveBoxScope (
    val adaptiveSize: AdaptiveSize,
    val aspectRatio:  Float,
)

@Composable
fun AdaptiveBox(
    modifier: Modifier = Modifier,
    content: @Composable AdaptiveBoxScope.() -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        val ratio = maxWidth / maxHeight
        val size  = getAdaptiveSize(ratio)
        AdaptiveBoxScope(size, ratio).content()
    }
}

private fun getAdaptiveSize(ratio: Float): AdaptiveSize = when {
    ratio >  1.8f -> AdaptiveSize.Wide
    ratio >= 1.0 && ratio < 1.8f -> AdaptiveSize.Landscape
    ratio <  1.0 && ratio > 0.5f -> AdaptiveSize.Portrait
    else -> AdaptiveSize.Thin
}
