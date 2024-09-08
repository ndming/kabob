package com.ndming.kabob.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI

class DrawArrowScope {
    var origin: Offset = Offset.Zero
        set(value) {
            field = value
            head += value
            tail  = value
        }

    var length: Float = 1.0f
        set(value) {
            field = value
            head = origin + Offset(1.0f, 0.0f).scale(value)
        }

    var head: Offset = Offset(1.0f, 0.0f)
        private set

    var tail: Offset = Offset.Zero
        private set

    fun rotate(radians: Float) {
        head = origin + Offset(1.0f, 0.0f).rotate(radians).scale(length)
    }

    fun translate(vector: Offset) {
        head += vector
        tail += vector
    }

    fun mapDrawSpace(canvasSize: Size, halfExtent: Float) {
        head = head.mapDrawSpace(canvasSize, halfExtent)
        tail = tail.mapDrawSpace(canvasSize, halfExtent)
    }
}

fun DrawScope.drawArrow(color: Color, block: DrawArrowScope.() -> Unit) {
    val scope = DrawArrowScope().apply(block)
    drawArrow(color, scope)
}

private fun DrawScope.drawArrow(color: Color, scope: DrawArrowScope) {
    val head = scope.head
    val tail = scope.tail

    val arrowLength = (head - tail).getDistance()
    val arrowTheta = (head - tail).getTheta()

    val arrowShaftHalfThickness = arrowLength * 0.01f
    val arrowHeadHeightLength = arrowLength * 0.2f
    val arrowHeadBaseLength = arrowHeadHeightLength * 1.0f

    val unitX = Offset(1.0f, 0.0f)
    val unitY = Offset(0.0f, 1.0f)

    val p0 = tail + unitY.rotate(arrowTheta).scale(arrowShaftHalfThickness)
    val p1 = p0 + unitX.rotate(arrowTheta).scale(arrowLength - arrowHeadHeightLength)
    val p2 = p1 + unitX.rotate(PI.toFloat() / 2.0f + arrowTheta).scale(arrowHeadBaseLength / 2.0f - arrowShaftHalfThickness)
    val p4 = p2 + unitX.rotate(arrowTheta - PI.toFloat() / 2.0f).scale(arrowHeadBaseLength)
    val p5 = p1 + unitX.rotate(arrowTheta - PI.toFloat() / 2.0f).scale(arrowShaftHalfThickness * 2.0f)
    val p6 = p0 + unitX.rotate(arrowTheta - PI.toFloat() / 2.0f).scale(arrowShaftHalfThickness * 2.0f)

    val path = Path().apply {
        moveTo(p0.x, p0.y)
        lineTo(p1.x, p1.y)
        lineTo(p2.x, p2.y)
        lineTo(head.x, head.y)
        lineTo(p4.x, p4.y)
        lineTo(p5.x, p5.y)
        lineTo(p6.x, p6.y)
        close()
    }

    drawCircle(color, radius = arrowLength / 2.0f, center = (head + tail) / 2.0f, style = Stroke(0.25f))
    drawPath(path, color)
}
