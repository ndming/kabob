package com.ndming.kabob.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke


/**
 * A scope used to define and manipulate segments for drawing purposes.
 *
 * This class provides properties to configure a drawable segment between two points (`start` and `end`),
 * including its transparency (`alpha`) and stroke width (`width`).
 */
class DrawSegmentScope {
    /**
     * The starting point of the segment.
     * Defaults to [Offset.Zero].
     */
    var start = Offset.Zero

    /**
     * The ending point of the segment.
     * Defaults to (1.0, 0.0) by default.
     */
    var end = Offset(1.0f, 0.0f)

    /**
     * The transparency of the segment.
     * A value between 0.0 (fully transparent) and 1.0 (fully opaque), with 1.0 as the default.
     */
    var alpha: Float = 1.0f

    /**
     * The width of the segment's stroke.
     * Defaults to [Stroke.HairlineWidth].
     */
    var width: Float = Stroke.HairlineWidth

    /**
     * Translates the current segment by the given [vector].
     *
     * Adds the [vector] to both the `start` and `end` points of the segment.
     *
     * @param vector The offset by which to move the segment.
     */
    fun translate(vector: Offset) {
        start += vector
        end += vector
    }

    /**
     * Maps the segment's coordinates to a specific drawing space.
     *
     * Adjusts the segment to fit within the bounds of a given canvas
     * size ([canvasSize]) and a specified half-extent ([halfExtent]).
     *
     * @param canvasSize The size of the canvas being drawn on.
     * @param halfExtent The radius of the draw space's extent.
     */
    fun mapDrawSpace(canvasSize: Size, halfExtent: Float) {
        start = start.mapDrawSpace(canvasSize, halfExtent)
        end = end.mapDrawSpace(canvasSize, halfExtent)
    }
}

/**
 * Draws a segment using the defined [DrawSegmentScope].
 *
 * @param color The color of the line segment.
 * @param block A lambda function to define and configure the [DrawSegmentScope].
 */
fun DrawScope.drawSegment(color: Color, block: DrawSegmentScope.() -> Unit) {
    val scope = DrawSegmentScope().apply(block)
    drawLine(color = color, start = scope.start, end = scope.end, strokeWidth = scope.width, alpha = scope.alpha)
}
