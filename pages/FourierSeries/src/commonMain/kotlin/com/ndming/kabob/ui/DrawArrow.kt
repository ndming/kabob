package com.ndming.kabob.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI

/**
 * A helper class for describing and manipulating a drawable arrow in 2D space.
 *
 * This class provides the capability to define the arrow's origin, length, head, and tail. It also includes utility
 * functions to rotate, translate, and map the arrow within a drawable space.
 *
 * @see [drawArrow].
 */
class DrawArrowScope {
    /**
     * The starting point (tail) of the arrow. Changing this will also recalculate the [head] and [tail].
     */
    var origin: Offset = Offset.Zero
        set(value) {
            field = value
            head += value
            tail  = value
        }

    /**
     * The length of the arrow. Adjusting this property scales the arrowhead proportionally.
     */
    var length: Float = 1.0f
        set(value) {
            field = value
            head = origin + Offset(1.0f, 0.0f).scale(value)
        }

    /**
     * The endpoint of the arrow. It is automatically updated when [origin] or [length] is set.
     */
    var head: Offset = Offset(1.0f, 0.0f)
        private set

    /**
     * The starting point of the arrow, initially set to the same point as [origin].
     */
    var tail: Offset = Offset.Zero
        private set

    /**
     * Rotates the arrow around its origin by a given angle in radians.
     */
    fun rotate(radians: Float) {
        head = origin + Offset(1.0f, 0.0f).rotate(radians).scale(length)
    }

    /**
     * Moves the arrow by a given vector, applying the same offset to its head and tail.
     */
    fun translate(vector: Offset) {
        head += vector
        tail += vector
    }

    /**
     * Maps the arrow coordinates into a drawable space defined by the canvas size and an arbitrary extent.
     */
    fun mapDrawSpace(canvasSize: Size, halfExtent: Float) {
        head = head.mapDrawSpace(canvasSize, halfExtent)
        tail = tail.mapDrawSpace(canvasSize, halfExtent)
    }
}

/**
 * Extension function for drawing an arrow on a [DrawScope].
 *
 * This function creates a [DrawArrowScope] instance, applies custom operations defined by [block], and
 * renders the arrow using the specified [color].
 *
 * @param color The color to use for drawing the arrow.
 * @param block A lambda function to define the transformations and properties of the arrow using [DrawArrowScope].
 */
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
