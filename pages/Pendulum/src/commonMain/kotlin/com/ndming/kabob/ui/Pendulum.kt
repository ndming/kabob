package com.ndming.kabob.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.coroutineScope
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Pendulum(
    theta: Float,
    modifier: Modifier = Modifier,
    fillHeightFirst: Boolean = false,
    showSettingButton: Boolean = false,
    onSettingButtonClick: () -> Unit = {},
    onRelease: () -> Unit = {},
    onDrag: (amount: Offset, upTimeMillis: Long, armLengthPx: Float) -> Unit,
) {
    val pendulumColor = MaterialTheme.colorScheme.tertiary
    val trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(ratio = 1f, matchHeightConstraintsFirst = fillHeightFirst)
            .drawBehind {
                // The dashed track
                drawCircle(
                    color = trackColor,
                    style = Stroke(
                        width = 3.0f, cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 20f)),
                    )
                )
                // The rope and the little dot at the center
                val ropeEnd = center + Offset(sin(theta), cos(theta)) * (size.minDimension / 2.0f)
                drawLine(pendulumColor, center, ropeEnd)
                drawCircle(pendulumColor, 5.0f)
            }
    ) {
        val pendulumRadius = (maxHeight * 0.06f).coerceAtLeast(16.dp)
        // The pendulum
        Box(
            modifier = Modifier
                .offset { pendulumIntOffset(theta, pendulumRadius, DpSize(maxWidth, maxHeight)) }
                .size(pendulumRadius * 2.0f)
                .clip(CircleShape)
                .background(pendulumColor)
                .pointerHoverIcon(PointerIcon.Hand)
                .pointerInput(Unit) {
                    detectPendulumDrag(onRelease) { amount, uptime ->
                        onDrag(amount, uptime, (maxHeight / 2.0f).toPx())
                    }
                }
        )

        // Setting button
        if (showSettingButton) {
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .pointerHoverIcon(PointerIcon.Hand),
                onClick = onSettingButtonClick,
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                )
            }
        }
    }
}

private fun Density.pendulumIntOffset(theta: Float, radius: Dp, size: DpSize): IntOffset {
    val (width, height) = size
    val length  = height / 2.0f
    val offsetX = sin(theta) * length + width  / 2.0f - radius
    val offsetY = cos(theta) * length + height / 2.0f - radius
    return IntOffset(offsetX.roundToPx(), offsetY.roundToPx())
}

private suspend fun PointerInputScope.detectPendulumDrag(
    onRelease: () -> Unit,
    onDrag: (amount: Offset, upTimeMillis: Long) -> Unit
) {
    // Use suspend functions for touch events
    coroutineScope {
        while (true) {
            awaitPointerEventScope {
                // Detect a touch-down event
                val pointerId = awaitFirstDown().id
                // Monitor its drag gesture
                drag(pointerId) { change ->
                    val amount = change.positionChange()
                    // Notify the drag amount and uptime
                    onDrag(amount, change.uptimeMillis)
                }
            }
            // No longer receiving touch events, drag has finishes
            onRelease()
        }
    }
}