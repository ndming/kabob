package com.ndming.kabob.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

fun Offset.getTheta(): Float = atan2(y, x)

fun Offset.rotate(radians: Float) = Offset(
    x = x * cos(radians) - y * sin(radians),
    y = x * sin(radians) + y * cos(radians),
)

fun Offset.scale(factor: Float) = Offset(x * factor, y * factor)

fun Offset.mapDrawSpace(canvasSize: Size, halfExtent: Float): Offset {
    val shortSide = if (canvasSize.width > canvasSize.height) canvasSize.height else canvasSize.width
    val scaleFactor = shortSide / 2.0f / halfExtent

    return Offset(x, -y).scale(scaleFactor) + Offset(canvasSize.width / 2.0f, canvasSize.height / 2.0f)
}
