package com.ndming.kabob.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween

fun themeTransitionSpec(): ContentTransform {
    val contentEnter = fadeIn(tween(800)) + slideInHorizontally(tween(600))
    val contentExit = fadeOut(tween(200)) + slideOutHorizontally(tween(400))
    return contentEnter.togetherWith(contentExit)
}