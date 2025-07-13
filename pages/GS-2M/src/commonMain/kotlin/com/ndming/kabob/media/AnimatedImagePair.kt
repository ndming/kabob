package com.ndming.kabob.media

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.ndming.kabob.gs_2m.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Data

@OptIn(ExperimentalResourceApi::class, ExperimentalComposeUiApi::class)
@Composable
fun AnimatedImagePair(
    filePathImgL: String,
    filePathImgR: String,
    fraction: Float,
    modifier: Modifier = Modifier,
    frameCount: Int = 200,
    frameDuration: Int = 1000 / 24,
    onFractionChange: (Float) -> Unit,
) {
    var bytesL by remember { mutableStateOf(ByteArray(0)) }
    var bytesR by remember { mutableStateOf(ByteArray(0)) }

    LaunchedEffect(filePathImgL) {
        bytesL = Res.readBytes(filePathImgL)
    }
    LaunchedEffect(filePathImgR) {
        bytesR = Res.readBytes(filePathImgR)
    }

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

    var boxWidth by remember { mutableStateOf(1f) }

    if (bytesL.isNotEmpty() && bytesR.isNotEmpty()) {
        val codecL = remember { Codec.makeFromData(Data.makeFromBytes(bytesL)) }
        val codecR = remember { Codec.makeFromData(Data.makeFromBytes(bytesR)) }

        val bitmapL = remember(codecL) { Bitmap().apply { allocPixels(codecL.imageInfo) } }
        val bitmapR = remember(codecR) { Bitmap().apply { allocPixels(codecR.imageInfo) } }
        remember(bitmapL, frameIndex) { codecL.readPixels(bitmapL, frameIndex) }
        remember(bitmapR, frameIndex) { codecR.readPixels(bitmapR, frameIndex) }

        Box(
            modifier = modifier
                .onGloballyPositioned { boxWidth = it.size.width.toFloat() }
                .onPointerEvent(PointerEventType.Move) { event ->
                    val position = event.changes.first().position
                    val width = boxWidth.coerceAtLeast(1f)
                    onFractionChange((position.x / width).coerceIn(0f, 1f))
                }
        ) {
            Image(
                bitmap = bitmapR.asComposeImageBitmap(),
                contentDescription = filePathImgR.substringAfter('/'),
                modifier = Modifier.fillMaxSize()
            )

            Image(
                bitmap = bitmapL.asComposeImageBitmap(),
                contentDescription = filePathImgL.substringAfter('/'),
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