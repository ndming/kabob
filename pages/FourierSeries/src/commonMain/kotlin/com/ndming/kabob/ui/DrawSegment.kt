package com.ndming.kabob.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

class DrawSegmentScope {
    var start = Offset.Zero
    var end = Offset(1.0f, 0.0f)

    var alpha: Float = 1.0f
    var width: Float = Stroke.HairlineWidth

    fun translate(vector: Offset) {
        start += vector
        end += vector
    }

    fun mapDrawSpace(canvasSize: Size, halfExtent: Float) {
        start = start.mapDrawSpace(canvasSize, halfExtent)
        end = end.mapDrawSpace(canvasSize, halfExtent)
    }
}

fun DrawScope.drawSegment(color: Color, block: DrawSegmentScope.() -> Unit) {
    val scope = DrawSegmentScope().apply(block)
    drawLine(color = color, start = scope.start, end = scope.end, strokeWidth = scope.width, alpha = scope.alpha)
}
