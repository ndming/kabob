package com.ndming.kabob.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwitchLeft
import androidx.compose.material.icons.filled.SwitchRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ndming.kabob.AnimatedImageState
import com.ndming.kabob.DecompositionMap
import com.ndming.kabob.Gs2mViewModel.Companion.DTU_SCENES
import com.ndming.kabob.Gs2mViewModel.Companion.SHINY_SCENES
import com.ndming.kabob.markup.HyperlinkText
import com.ndming.kabob.media.AnimatedImage
import com.ndming.kabob.media.AnimatedImagePair
import com.ndming.kabob.theme.getJetBrainsMonoFamily

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PaperCover(
    dtuViewerSceneIndex: Int,
    dtuViewerSceneState: AnimatedImageState,
    shinyViewerSceneIndex: Int,
    shinyViewerSceneState: AnimatedImageState,
    shinyViewerPairedMap: DecompositionMap,
    portrait: Boolean,
    modifier: Modifier = Modifier,
    onDtuViewerFrameRequest: (Int) -> ImageBitmap,
    onDtuViewerSceneChange: (Int) -> Unit,
    onShinyViewerFrameRequest: (Int) -> Pair<ImageBitmap, ImageBitmap>,
    onShinyViewerSceneChange: (Int) -> Unit,
    onShinyViewerPairedMapChange: (DecompositionMap) -> Unit,
) {
    if (portrait) {
        Column(
            modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DtuViewer(
                sceneIndex = dtuViewerSceneIndex,
                sceneState = dtuViewerSceneState,
                modifier = Modifier.fillMaxWidth(),
                onFrameRequest = onDtuViewerFrameRequest,
                onSceneChange = onDtuViewerSceneChange,
            )

            Spacer(Modifier.height(56.dp))

            ShinyViewer(
                sceneIndex = shinyViewerSceneIndex,
                sceneState = shinyViewerSceneState,
                pairedMap = shinyViewerPairedMap,
                portrait = portrait,
                modifier = Modifier.fillMaxWidth(),
                onFrameRequest = onShinyViewerFrameRequest,
                onSceneChange = onShinyViewerSceneChange,
                onPairedMapChange = onShinyViewerPairedMapChange,
            )
        }
    } else {
        FlowRow(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Row {
                Spacer(Modifier.width(48.dp))
                DtuViewer(
                    sceneIndex = dtuViewerSceneIndex,
                    sceneState = dtuViewerSceneState,
                    modifier = Modifier.width(480.dp),
                    onFrameRequest = onDtuViewerFrameRequest,
                    onSceneChange = onDtuViewerSceneChange,
                )
                Spacer(Modifier.width(48.dp))
            }

            Row {
                Spacer(Modifier.width(48.dp))
                ShinyViewer(
                    sceneIndex = shinyViewerSceneIndex,
                    sceneState = shinyViewerSceneState,
                    pairedMap = shinyViewerPairedMap,
                    portrait = portrait,
                    modifier = Modifier.width(480.dp),
                    onFrameRequest = onShinyViewerFrameRequest,
                    onSceneChange = onShinyViewerSceneChange,
                    onPairedMapChange = onShinyViewerPairedMapChange,
                )
                Spacer(Modifier.width(48.dp))
            }
        }
    }
}

@Composable
private fun DtuViewer(
    sceneIndex: Int,
    sceneState: AnimatedImageState,
    modifier: Modifier = Modifier,
    onFrameRequest: (Int) -> ImageBitmap,
    onSceneChange: (Int) -> Unit,
) {
    val monoFamily = getJetBrainsMonoFamily()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(1.0f)) {
            @Suppress("HttpUrlsUsage")
            HyperlinkText(
                linkText = "DTU",
                linkUrl = "http://roboimagedata.compute.dtu.dk/?page_id=36",
                prefix = "Reconstruction on the ",
                suffix = " dataset",
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 32.dp),
            )

            AnimatedImage(
                modifier = Modifier.align(Alignment.Center),
                loading = sceneState.loading,
                missing = sceneState.missing,
                contentDescription = null,
                onFrameRequest = onFrameRequest,
            )
        }

        Spacer(Modifier.height(16.dp))

        Switcher(
            sceneName = "scan ${DTU_SCENES[sceneIndex]}",
            onSwitchL = { onSceneChange(if (sceneIndex == 0) DTU_SCENES.lastIndex else sceneIndex - 1) },
            onSwitchR = { onSceneChange((sceneIndex + 1) % DTU_SCENES.size) },
            fontFamily = monoFamily,
        )
    }
}

@Composable
private fun ShinyViewer(
    sceneIndex: Int,
    sceneState: AnimatedImageState,
    pairedMap: DecompositionMap,
    portrait: Boolean,
    modifier: Modifier = Modifier,
    onFrameRequest: (Int) -> Pair<ImageBitmap, ImageBitmap>,
    onSceneChange: (Int) -> Unit,
    onPairedMapChange: (DecompositionMap) -> Unit,
) {
    val monoFamily = getJetBrainsMonoFamily()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(1.0f)) {
            HyperlinkText(
                linkText = "ShinyBlender",
                linkUrl = "https://dorverbin.github.io/refnerf/",
                prefix = "Decomposition on the ",
                suffix = " dataset",
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 32.dp),
            )

            AnimatedImagePair(
                modifier = Modifier.padding(32.dp).align(Alignment.Center),
                loading = sceneState.loading,
                missing = sceneState.missing,
                contentDescriptionL = null,
                contentDescriptionR = null,
                onFrameRequest = onFrameRequest,
            )

            SingleChoiceSegmentedButtonRow(modifier = Modifier.align(Alignment.BottomCenter)) {
                DecompositionMap.entries.forEach { map ->
                    SegmentedButton(
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                        shape = SegmentedButtonDefaults.itemShape(
                            index = map.ordinal,
                            count = DecompositionMap.entries.size
                        ),
                        onClick = { onPairedMapChange(map) },
                        selected = pairedMap == map,
                        label = { DisableSelection { if (!portrait) Text(map.fullName) else Text(map.shortName) } }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Switcher(
            sceneName = SHINY_SCENES[sceneIndex],
            onSwitchL = { onSceneChange(if (sceneIndex == 0) SHINY_SCENES.lastIndex else sceneIndex - 1) },
            onSwitchR = { onSceneChange((sceneIndex + 1) % SHINY_SCENES.size) },
            fontFamily = monoFamily,
        )
    }
}

@Composable
private fun Switcher(
    sceneName: String,
    fontFamily: FontFamily,
    modifier: Modifier = Modifier,
    onSwitchL: () -> Unit,
    onSwitchR: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Spacer(Modifier.width(72.dp))
            FilledTonalButton(
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                onClick = onSwitchL,
            ) {
                Icon(Icons.Default.SwitchLeft,null)
            }
        }

        AnimatedContent(
            targetState = sceneName,
            contentAlignment = Alignment.Center,
            transitionSpec = {
                val contentEnter = fadeIn(tween(800)) + slideInVertically(tween(600)) { -it / 2 }
                val contentExit = fadeOut(tween(200)) + slideOutVertically(tween(400)) { it / 2 }
                contentEnter.togetherWith(contentExit)
            },
        ) { name ->
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Medium,
            )
        }

        Row {
            FilledTonalButton(
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                onClick = onSwitchR,
            ) {
                Icon(Icons.Default.SwitchRight,null)
            }
            Spacer(Modifier.width(72.dp))
        }
    }
}