package com.ndming.kabob.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.ndming.kabob.AnimatedImageState
import com.ndming.kabob.Gs2mViewModel.Companion.SHINY_MESHES
import com.ndming.kabob.ReconstructionMethod
import com.ndming.kabob.media.ImagePair
import com.ndming.kabob.theme.getJetBrainsMonoFamily

@Composable
fun ShinyMeshComparison(
    sceneIndex: Int,
    sceneState: AnimatedImageState,
    pairedMethod: ReconstructionMethod,
    portrait: Boolean,
    modifier: Modifier = Modifier,
    onFrameRequest: (Int) -> Triple<ImageBitmap, ImageBitmap, ImageBitmap>,
    onSceneChange: (Int) -> Unit,
    onPairedMethodChange: (ReconstructionMethod) -> Unit,
) {
    val transition = rememberInfiniteTransition()
    val frameIndex by transition.animateValue(
        initialValue = 0,
        targetValue = 200 - 1,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = 0
                repeat(200) {
                    it at durationMillis
                    durationMillis += (1000 / 24)
                }
            }
        )
    )

    val extra = !sceneState.loading && !sceneState.missing && !portrait
    val (bitmapGT, bitmapL, bitmapR) = if (!sceneState.loading && !sceneState.missing) {
        onFrameRequest(frameIndex)
    } else {
        Triple(null, null, null)
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        if (extra) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    bitmap = bitmapGT!!,
                    contentDescription = null,
                    modifier = Modifier.width(256.dp),
                )
                Text(
                    text = "Ground Truth",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }

        ShinyMeshViewer(
            sceneIndex = sceneIndex,
            sceneState = sceneState,
            pairedMethod = pairedMethod,
            bitmapL = bitmapL,
            bitmapR = bitmapR,
            portrait = portrait,
            onSceneChange = onSceneChange,
            onPairedMethodChange = onPairedMethodChange,
        )

        if (extra) {
            Card(
                modifier = Modifier.width(256.dp),
            ) {
                ReferenceEntry(
                    method = pairedMethod,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}

@Composable
private fun ShinyMeshViewer(
    sceneIndex: Int,
    sceneState: AnimatedImageState,
    pairedMethod: ReconstructionMethod,
    bitmapL: ImageBitmap?,
    bitmapR: ImageBitmap?,
    portrait: Boolean,
    modifier: Modifier = Modifier,
    onSceneChange: (Int) -> Unit,
    onPairedMethodChange: (ReconstructionMethod) -> Unit,
) {
    val monoFamily = getJetBrainsMonoFamily()
    val labelStyle = if (portrait) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.headlineMedium

    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = if (portrait) {
                Modifier.fillMaxWidth().aspectRatio(1.0f)
            } else {
                Modifier.width(560.dp).aspectRatio(1.0f)
            }
        ) {
            ImagePair(
                bitmapL = bitmapL,
                bitmapR = bitmapR,
                loading = sceneState.loading,
                missing = sceneState.missing,
                contentDescriptionL = null,
                contentDescriptionR = null,
                modifier = Modifier.align(Alignment.Center),
            )

            Text(
                text = "Ours",
                modifier = Modifier.align(Alignment.BottomStart).padding(start = 4.dp),
                style = labelStyle,
            )

            Surface(
                modifier = Modifier
                    .wrapContentSize(Alignment.Center)
                    .align(Alignment.BottomEnd)
                    .clickable {
                        val currentMethodIndex = pairedMethod.ordinal
                        val targetMethodIndex = (currentMethodIndex + 1) % ReconstructionMethod.entries.size
                        onPairedMethodChange(ReconstructionMethod.entries[targetMethodIndex])
                    }
                    .pointerHoverIcon(PointerIcon.Hand),
                shape = RoundedCornerShape(16),
                border = BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.primary),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    DisableSelection {
                        Text(
                            text = pairedMethod.prefix,
                            style = labelStyle,
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = null,
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Switcher(
            sceneName = SHINY_MESHES[sceneIndex],
            onSwitchL = { onSceneChange(if (sceneIndex == 0) SHINY_MESHES.lastIndex else sceneIndex - 1) },
            onSwitchR = { onSceneChange((sceneIndex + 1) % SHINY_MESHES.size) },
            fontFamily = monoFamily,
            portrait = portrait,
            modifier = Modifier.width(480.dp)
        )
    }
}

@Composable
private fun ReferenceEntry(
    method: ReconstructionMethod,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = buildAnnotatedString {
            append(method.authors + " ")
            append("(" + method.year + "). ")
            append(method.title + ". ")
            withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                append(method.venue)
            }
            append(".")
        }
    )
}