package com.ndming.kabob.media

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AnimatedImagePair(
    loading: Boolean,
    missing: Boolean,
    contentDescriptionL: String?,
    contentDescriptionR: String?,
    modifier: Modifier = Modifier,
    frameCount: Int = 200,
    frameDuration: Int = 1000 / 24,
    onFrameRequest: (Int) -> Pair<ImageBitmap, ImageBitmap>,
) {
    val transition = rememberInfiniteTransition()
    val frameIndex by transition.animateValue(
        initialValue = 0,
        targetValue = frameCount - 1,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = 0
                repeat(frameCount) {
                    it at durationMillis
                    durationMillis += frameDuration
                }
            }
        )
    )

    var fraction by remember { mutableStateOf(0.5f) }
    var boxWidth by remember { mutableStateOf(1f) }

    if (loading) {
        CircularProgressIndicator(modifier = modifier)
    } else if (missing) {
        Icon(
            imageVector = Icons.Default.Engineering,
            contentDescription = null,
            modifier = modifier.size(64.dp),
            tint = LocalContentColor.current.copy(alpha = 0.6f)
        )
    } else {
        val (bitmapL, bitmapR) = onFrameRequest(frameIndex)
        Box(
            modifier = modifier
                .onGloballyPositioned { boxWidth = it.size.width.toFloat() }
                .onPointerEvent(PointerEventType.Move) { event ->
                    val position = event.changes.first().position
                    val width = boxWidth.coerceAtLeast(1f)
                    fraction = (position.x / width).coerceIn(0f, 1f)
                }
        ) {
            Image(
                bitmap = bitmapR,
                contentDescription = contentDescriptionR,
                modifier = Modifier.fillMaxSize()
            )

            Image(
                bitmap = bitmapL,
                contentDescription = contentDescriptionL,
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithContent {
                        val width = size.width * fraction
                        clipRect(left = 0f, right = width) {
                            this@drawWithContent.drawContent()
                        }
                    }
            )

            val arrowSize = 24.dp
            val xOffset = with(LocalDensity.current) { (boxWidth * fraction).toDp() }

            Box(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .offset(x = xOffset - arrowSize - 6.dp)
                        .align(Alignment.CenterStart)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                            shape = CircleShape)
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                        .pointerHoverIcon(PointerIcon.Hand),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(arrowSize)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(arrowSize)
                    )
                }
            }
        }
    }
}