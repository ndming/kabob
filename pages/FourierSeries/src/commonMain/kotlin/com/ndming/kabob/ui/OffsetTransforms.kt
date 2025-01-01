package com.ndming.kabob.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Calculates the angle (theta) of the vector from the origin (0, 0) in radians.
 *
 * @return Angle in radians, measured counterclockwise from the positive x-axis.
 */
fun Offset.getTheta(): Float = atan2(y, x)

/**
 * Rotates this offset vector by the specified angle in radians.
 *
 * @param radians The angle to rotate the offset by, in radians.
 * @return A new Offset representing the rotated vector.
 */
fun Offset.rotate(radians: Float) = Offset(
    x = x * cos(radians) - y * sin(radians),
    y = x * sin(radians) + y * cos(radians),
)

/**
 * Scales this offset vector by the specified scaling factor.
 *
 * @param factor The factor by which to scale the offset.
 * @return A new Offset representing the scaled vector.
 */
fun Offset.scale(factor: Float) = Offset(x * factor, y * factor)

/**
 * Maps this offset from a logical space into the drawing space.
 *
 * @param canvasSize The size of the canvas where this offset will be drawn.
 * @param halfExtent The half-extent of the logical space to be mapped.
 * @return A new Offset mapped to the drawing space.
 */
fun Offset.mapDrawSpace(canvasSize: Size, halfExtent: Float): Offset {
    val shortSide = if (canvasSize.width > canvasSize.height) canvasSize.height else canvasSize.width
    val scaleFactor = shortSide / 2.0f / halfExtent

    return Offset(x, -y).scale(scaleFactor) + Offset(canvasSize.width / 2.0f, canvasSize.height / 2.0f)
}
