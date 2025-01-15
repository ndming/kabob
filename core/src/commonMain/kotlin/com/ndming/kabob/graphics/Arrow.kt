package com.ndming.kabob.graphics

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.atan2

class DrawArrowScope {
    /**
     * The starting point (tail) of the arrow. Changing this will also recalculate its [head] and [tail].
     */
    var origin: Offset = Offset.Zero
        set(value) {
            field = value
            head += value
            tail  = value
        }

    /**
     * The length of the arrow. Adjusting this property scales the arrow's [head] accordingly.
     */
    var length: Float = 1.0f
        set(value) {
            field = value
            head = origin + Offset(1.0f, 0.0f) * value
        }

    /**
     * The arrowhead of the arrow. It is automatically updated when [origin] or [length] is set.
     */
    var head: Offset = Offset(1.0f, 0.0f)
        private set

    /**
     * The starting point of the arrow shaft, initially set to the same point as [origin].
     */
    var tail: Offset = Offset.Zero
        private set

    /**
     * Rotates the arrow around its origin by a given angle in radians.
     */
    fun rotate(radians: Float) {
        head = origin + Offset(1.0f, 0.0f).rotate(radians) * length
    }

    /**
     * Moves the arrow by a given vector, applying the same offset to its head and tail.
     */
    fun translate(vector: Offset) {
        head += vector
        tail += vector
    }

    /**
     * Maps the arrow coordinates into a viewport defined by a [size] and an arbitrary [halfExtent].
     * @see viewport
     */
    fun toViewport(size: Size, halfExtent: Float) {
        head = head.viewport(size, halfExtent)
        tail = tail.viewport(size, halfExtent)
    }
}

fun DrawScope.drawArrow(
    color: Color,
    withCircle: Boolean = true,
    headRatio: Float = 0.2f,
    block: DrawArrowScope.() -> Unit,
) {
    val scope = DrawArrowScope().apply(block)
    drawArrow(color, withCircle, headRatio, scope)
}

private fun DrawScope.drawArrow(color: Color, withCircle: Boolean, headRatio: Float, scope: DrawArrowScope) {
    val head = scope.head
    val tail = scope.tail

    val length = (head - tail).getDistance()
    val theta  = (head - tail).let { (x, y) -> atan2(y, x) }

    val base = tail + UNIT_X.rotate(theta) * length * (1.0f - headRatio)

    val p0 = base + UNIT_Y.rotate(theta) * (length * headRatio / 2.0f)
    val p1 = p0 + UNIT_X.rotate(theta - PI.toFloat() / 2.0f) * length * headRatio
    val arrowHead = Path().apply {
        moveTo(p0.x, p0.y)
        lineTo(head.x, head.y)
        lineTo(p1.x, p1.y)
        close()
    }

    if (withCircle) {
        drawCircle(
            color  = color.copy(0.4f),
            style  = Stroke(0.8f),
            radius = length / 2.0f,
            center = (head + tail) / 2.0f,
        )
    }
    drawLine(color, tail, base, (length * 0.06f).coerceAtMost(1.6f))
    drawPath(arrowHead, color)
}

private val UNIT_X = Offset(1.0f, 0.0f)
private val UNIT_Y = Offset(0.0f, 1.0f)