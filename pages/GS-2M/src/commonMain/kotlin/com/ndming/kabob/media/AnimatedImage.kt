package com.ndming.kabob.media

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeImageBitmap
import com.ndming.kabob.gs_2m.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Data

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AnimatedImage(
    filePath: String,
    modifier: Modifier = Modifier,
) {
    var bytes by remember { mutableStateOf(ByteArray(0)) }
    LaunchedEffect(Unit) {
        bytes = Res.readBytes(filePath)
    }

    if (bytes.isNotEmpty()) {
        val codec = remember { Codec.makeFromData(Data.makeFromBytes(bytes)) }

        val transition = rememberInfiniteTransition()
        val frameIndex by transition.animateValue(
            initialValue = 0,
            targetValue = codec.frameCount - 1,
            typeConverter = Int.VectorConverter,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 0
                    for ((index, frame) in codec.framesInfo.withIndex()) {
                        index at durationMillis
                        val frameDuration = frame.duration
                        durationMillis += frameDuration
                    }
                }
            )
        )

        val bitmap = remember(codec) { Bitmap().apply { allocPixels(codec.imageInfo) } }
        remember(bitmap, frameIndex) { codec.readPixels(bitmap, frameIndex) }

        Image(
            bitmap = bitmap.asComposeImageBitmap(),
            contentDescription = filePath.substringAfter('/'),
            modifier = modifier,
        )
    }
}