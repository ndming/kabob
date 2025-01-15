package com.ndming.kabob.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.util.fastRoundToInt
import com.ndming.kabob.graphics.drawArrow
import com.ndming.kabob.graphics.viewport
import com.ndming.kabob.theme.getNotoSansMathFamily
import org.jetbrains.kotlinx.multik.api.arange
import org.jetbrains.kotlinx.multik.api.mk
import kotlin.math.*

private const val VECTOR_LENGTH_CUTOFF = 8.0f
private const val TICK_CUTOFF_DISTANCE = 2.0f
private const val AXIS_CUTOFF_DISTANCE = 0.25f

private const val GRAVITY = 9.810f
private const val SPACING = 0.5f

@Composable
fun PhaseSpace(
    armLength: Float,
    friction: Float,
    theta: Float,
    thetaDot: Float,
    modifier: Modifier = Modifier,
    xCenter: Float = 0.0f,
    yScale: Float = 1.0f,
    yLimit: Float = 4.0f,
    viewportAspectRatio: Float = 1.3f,
) {
    val xLimit = (yLimit * viewportAspectRatio)

    val xSamples = mk.arange<Float>(
        start = (-xLimit + xCenter).fastRoundToInt() - 1,
        stop = (xLimit + xCenter).fastRoundToInt() + 2,
        step = SPACING.toDouble()
    )
    val ySamples = mk.arange<Float>(
        start = -yLimit.fastRoundToInt(),
        stop  = yLimit.fastRoundToInt() + 1,
        step  = SPACING.toDouble()
    )

    val xTickMultiplierMin = floor((-xLimit + xCenter) / PI.toFloat()).fastRoundToInt()
    val xTickMultiplierMax =  ceil(( xLimit + xCenter) / PI.toFloat()).fastRoundToInt()
    val xTickMultipliers = mk.arange<Float>(xTickMultiplierMin, xTickMultiplierMax + 1, 1.0)

    val yTicks = mk.arange<Float>(1, yLimit.fastRoundToInt(), 1.0)

    val arrowColor = MaterialTheme.colorScheme.tertiary
    val frameColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    val stateColor = MaterialTheme.colorScheme.primary

    val textMeasurer = rememberTextMeasurer()

    val labelStyle = MaterialTheme.typography.displaySmall.copy(
        fontFamily = getNotoSansMathFamily(),
        color = frameColor.copy(alpha = 1.0f),
    )
    val tickStyle = MaterialTheme.typography.headlineSmall.copy(
        fontFamily = getNotoSansMathFamily(),
        color = frameColor.copy(alpha = 1.0f),
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val fontSizeFactor = size.width * 9e-4f

        val xLabelOffsetFactor = size.width * 6e-4f
        val yLabelOffsetFactor = 1e3f / size.width

        // x-axis
        drawLine(
            color = frameColor,
            start = Offset(-xLimit + AXIS_CUTOFF_DISTANCE, 0.0f).viewport(size, yLimit),
            end   = Offset( xLimit - AXIS_CUTOFF_DISTANCE, 0.0f).viewport(size, yLimit),
            strokeWidth = 1.5f,
            cap = StrokeCap.Round,
        )

        // The vector field
        for (x in xSamples) {
            for (y in ySamples) {
                val vX = y * yScale
                val vY = -friction * y * yScale - (GRAVITY / armLength) * sin(x)

                val magnitude = sqrt(vX * vX + vY * vY)
                val alpha = (magnitude / VECTOR_LENGTH_CUTOFF).coerceAtMost(1.0f)

                drawArrow(color = arrowColor.copy(alpha = alpha), withCircle = false, headRatio = 0.3f) {
                    origin = Offset(x, y)
                    length = SPACING * 0.6f
                    rotate(atan2(vY, vX))
                    translate(Offset(-xCenter, 0.0f))
                    toViewport(size, yLimit)
                }
            }
        }

        // x ticks
        for (multiplier in xTickMultipliers) {
            val tick = multiplier * PI.toFloat() - xCenter
            val scaleFactor = when {
                abs(tick) > xLimit - AXIS_CUTOFF_DISTANCE -> 0.0f
                abs(tick) < xLimit - TICK_CUTOFF_DISTANCE -> 1.0f
                else -> 1.0f - (abs(tick) - xLimit + TICK_CUTOFF_DISTANCE) / (TICK_CUTOFF_DISTANCE - AXIS_CUTOFF_DISTANCE)
            }

            // Tick mark
            drawLine(
                color = frameColor.copy(alpha = scaleFactor * 0.5f),
                start = Offset(tick, -0.08f * scaleFactor).viewport(size, yLimit),
                end   = Offset(tick,  0.08f * scaleFactor).viewport(size, yLimit),
                strokeWidth = 2.5f,
                cap = StrokeCap.Round,
            )

            // Tick label
            val tickLabel = if (multiplier < 0) {
                "\u2212" + if(multiplier < -1) abs(multiplier).toInt().toString() + "\uD835\uDF0B" else "\uD835\uDF0B"
            } else if (multiplier > 1) {
                multiplier.toInt().toString() + "\uD835\uDF0B"
            } else if (multiplier == 1.0f) {
                "\uD835\uDF0B"
            } else {
                multiplier.toInt().toString()
            }
            val tickOffset = if (multiplier > 0) 0.15f else if (multiplier < 0) 0.25f else 0.06f
            drawText(
                textMeasurer = textMeasurer,
                text = tickLabel,
                style = tickStyle.copy(fontSize = tickStyle.fontSize * scaleFactor * fontSizeFactor),
                size = Size(100.0f, 30.0f),
                topLeft = Offset(
                    x = tick - tickOffset * scaleFactor,
                    y = -0.2f * scaleFactor * xLabelOffsetFactor
                ).viewport(size, yLimit),
            )
        }

        // y ticks
        for (tick in yTicks) {
            // Positive tick
            drawLine(
                color = frameColor,
                start = Offset(-xLimit - 0.01f, tick).viewport(size, yLimit),
                end   = Offset(-xLimit + 0.08f, tick).viewport(size, yLimit),
                strokeWidth = 2.5f,
                cap = StrokeCap.Round,
            )
            // Negative tick
            drawLine(
                color = frameColor,
                start = Offset(-xLimit - 0.02f, -tick).viewport(size, yLimit),
                end   = Offset(-xLimit + 0.08f, -tick).viewport(size, yLimit),
                strokeWidth = 2.5f,
                cap = StrokeCap.Round,
            )

            // Tick labels
            val tickLabel = (tick * yScale).fastRoundToInt().toString()

            drawText(
                textMeasurer = textMeasurer,
                text = tickLabel,
                style = tickStyle.copy(fontSize = tickStyle.fontSize * fontSizeFactor),
                size = Size(60.0f, 30.0f),
                topLeft = Offset(-xLimit + 0.16f, tick + 0.16f * yLabelOffsetFactor).viewport(size, yLimit),
            )
            drawText(
                textMeasurer = textMeasurer,
                text = "\u2212" + tickLabel,
                style = tickStyle.copy(fontSize = tickStyle.fontSize * fontSizeFactor),
                size = Size(60.0f, 30.0f),
                topLeft = Offset(-xLimit + 0.16f, -tick + 0.16f * yLabelOffsetFactor).viewport(size, yLimit),
            )
        }


        // x-axis label
        drawText(
            textMeasurer = textMeasurer,
            topLeft = Offset(size.width - 50.0f * xLabelOffsetFactor, size.height / 2.0f + 10.0f * xLabelOffsetFactor),
            text = "\uD835\uDF03",
            style = labelStyle.copy(fontSize = labelStyle.fontSize * fontSizeFactor),
        )
        // y-axis label
        drawText(
            textMeasurer = textMeasurer,
            topLeft = Offset(30.0f * xLabelOffsetFactor, 10.0f * xLabelOffsetFactor),
            text = "\uD835\uDF03'",
            style = labelStyle.copy(fontSize = labelStyle.fontSize * fontSizeFactor),
        )

        // Pendulum state
        drawPoints(
            color = stateColor,
            points = listOf(Offset(theta - xCenter, thetaDot / yScale).viewport(size, yLimit)),
            pointMode = PointMode.Points,
            cap = StrokeCap.Round,
            strokeWidth = size.width * 0.016f,
        )
    }
}
