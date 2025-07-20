package com.ndming.kabob.media

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedImage(
    loading: Boolean,
    missing: Boolean,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    frameCount: Int = 120,
    frameDuration: Int = 1000 / 24,
    onFrameRequest: (Int) -> ImageBitmap,
) {
    val transition = rememberInfiniteTransition()
    val frameIndex by transition.animateValue(
        initialValue = 0,
        targetValue = frameCount - 1,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 0
                repeat(frameCount) {
                    it at durationMillis
                    durationMillis += frameDuration
                }
            }
        )
    )

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
        Image(
            bitmap = onFrameRequest(frameIndex),
            contentDescription = contentDescription,
            modifier = modifier,
        )
    }

//    if (bytes.isNotEmpty()) {
//        val codec = remember(bytes) { Codec.makeFromData(Data.makeFromBytes(bytes)) }
//
//        val bitmap = remember(codec) { Bitmap().apply { allocPixels(codec.imageInfo) } }
//        remember(bitmap, frameIndex) { codec.readPixels(bitmap, frameIndex) }
//
//        Image(
//            bitmap = bitmap.asComposeImageBitmap(),
//            contentDescription = filePath.substringAfter('/'),
//            modifier = modifier,
//        )
//    } else if (missing) {
//        Icon(
//            imageVector = Icons.Default.Engineering,
//            contentDescription = null,
//            modifier = Modifier.size(64.dp),
//            tint = LocalContentColor.current.copy(alpha = 0.6f)
//        )
//    } else {
//        CircularProgressIndicator()
//    }
}