package com.ndming.kabob.graphics

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

class DrawSegmentScope {
    /**
     * The starting coordinates of the segment.
     * Defaults to [Offset.Zero].
     */
    var start = Offset.Zero

    /**
     * The ending coordinates of the segment.
     * Defaults to (1.0, 0.0) by default.
     */
    var end = Offset(1.0f, 0.0f)

    /**
     * The transparency of the segment - a value between 0.0 (fully transparent) and 1.0 (fully opaque),
     * with 1.0 as the default.
     */
    var alpha: Float = 1.0f

    /**
     * The width of the segment's stroke, default to [Stroke.HairlineWidth].
     */
    var width: Float = Stroke.HairlineWidth

    /**
     * Translates the current segment by the given [vector].
     *
     * @param vector The offset by which to move the segment.
     */
    fun translate(vector: Offset) {
        start += vector
        end += vector
    }

    /**
     * Maps the segment's coordinates to a viewport defined by a [size] and an arbitrary [halfExtent].
     * @see viewport
     */
    fun toViewport(size: Size, halfExtent: Float) {
        start = start.viewport(size, halfExtent)
        end   = end.viewport(size, halfExtent)
    }
}

fun DrawScope.drawSegment(color: Color, block: DrawSegmentScope.() -> Unit) {
    val scope = DrawSegmentScope().apply(block)
    drawLine(color = color, start = scope.start, end = scope.end, strokeWidth = scope.width, alpha = scope.alpha)
}