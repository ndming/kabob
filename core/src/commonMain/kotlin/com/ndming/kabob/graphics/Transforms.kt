package com.ndming.kabob.graphics

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.cos
import kotlin.math.sin

/**
 * Returns a new [Offset] as a result of rotating this offset by [radians].
 */
fun Offset.rotate(radians: Float) = Offset(
    x = x * cos(radians) - y * sin(radians),
    y = x * sin(radians) + y * cos(radians),
)

/**
 * Returns a new [Offset] as a result of scaling this offset's components by the specified [scalar].
 */
fun Offset.scale(scalar: Float) = Offset(x * scalar, y * scalar)

/**
 * Returns a new [Offset] as a result of mapping this offset from its local (object) space to view (camera) space.
 * The transform depends on the viewport's size and how much of the 2D plane that is visible within this viewport.
 *
 * @param size The size of the viewport (width, height) where this offset will be drawn. When the offset is to be used
 * within a [DrawScope], the [DrawScope.size] provides the precise dimensions for this parameter.
 * @param halfExtent The maximum extent visible in the viewport, either in the x- or y-directions, depending on the
 * viewport's [size]. A `halfExtent` value of `5.0` for a viewport whose `width` > `height`, for example, scales the
 * view space such that the horizontal edges of the viewport placed at `-5.0` and `5.0`, whereas for a viewport whose
 * `width` < `height` the vertical edges will be placed at `-5.0` and `5.0`.
 */
fun Offset.viewport(size: Size, halfExtent: Float): Offset {
    val shortSide = if (size.width > size.height) size.height else size.width
    val scaleFactor = shortSide / 2.0f / halfExtent
    return Offset(x, -y).scale(scaleFactor) + Offset(size.width / 2.0f, size.height / 2.0f)
}