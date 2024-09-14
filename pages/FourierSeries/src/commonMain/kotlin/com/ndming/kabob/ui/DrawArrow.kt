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

    val length = (head - tail).getDistance()
    val theta = (head - tail).getTheta()

    val base = tail + UNIT_X.rotate(theta).scale(length * 0.8f)

    val p0 = base + UNIT_Y.rotate(theta).scale(length * 0.2f / 2.0f)
    val p1 = p0 + UNIT_X.rotate(theta - PI.toFloat() / 2.0f).scale(length * 0.2f)
    val arrowHead = Path().apply {
        moveTo(p0.x, p0.y)
        lineTo(head.x, head.y)
        lineTo(p1.x, p1.y)
        close()
    }

    drawCircle(color.copy(0.4f), radius = length / 2.0f, center = (head + tail) / 2.0f, style = Stroke(0.8f))
    drawLine(color, tail, base, (length * 0.06f).coerceAtMost(1.6f))
    drawPath(arrowHead, color)
}

private val UNIT_X = Offset(1.0f, 0.0f)
private val UNIT_Y = Offset(0.0f, 1.0f)
