package com.ndming.kabob.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import com.ndming.kabob.AnimatedImageState
import com.ndming.kabob.DecompositionMap
import com.ndming.kabob.Gs2mViewModel.Companion.DTU_SCENES
import com.ndming.kabob.Gs2mViewModel.Companion.SHINY_SCENES
import com.ndming.kabob.markup.HyperlinkText
import com.ndming.kabob.media.AnimatedImage
import com.ndming.kabob.media.ImagePair
import com.ndming.kabob.theme.getJetBrainsMonoFamily
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PaperCover(
    dtuViewerBitmapGT: ImageBitmap?,
    dtuViewerSceneIndex: Int,
    dtuViewerSceneState: AnimatedImageState,
    shinyViewerBitmapL: ImageBitmap?,
    shinyViewerBitmapR: ImageBitmap?,
    shinyViewerSceneIndex: Int,
    shinyViewerSceneState: AnimatedImageState,
    shinyViewerPairedMap: DecompositionMap,
    shinyViewerPlaying: Boolean,
    portrait: Boolean,
    modifier: Modifier = Modifier,
    onDtuViewerFrameRequest: (Int) -> ImageBitmap,
    onDtuViewerSceneChange: (Int) -> Unit,
    onShinyViewerSceneChange: (Int) -> Unit,
    onShinyViewerPairedMapChange: (DecompositionMap) -> Unit,
    onShinyViewerPlayingChange: (Boolean, CoroutineScope) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    if (portrait) {
        Column(
            modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DtuViewer(
                bitmapGT = dtuViewerBitmapGT,
                sceneIndex = dtuViewerSceneIndex,
                sceneState = dtuViewerSceneState,
                modifier = Modifier.fillMaxWidth(),
                onFrameRequest = onDtuViewerFrameRequest,
                onSceneChange = onDtuViewerSceneChange,
                portrait = portrait,
            )

            Spacer(Modifier.height(56.dp))

            ShinyViewer(
                bitmapL = shinyViewerBitmapL,
                bitmapR = shinyViewerBitmapR,
                sceneIndex = shinyViewerSceneIndex,
                sceneState = shinyViewerSceneState,
                pairedMap = shinyViewerPairedMap,
                playing = shinyViewerPlaying,
                portrait = portrait,
                modifier = Modifier.fillMaxWidth(),
                onSceneChange = onShinyViewerSceneChange,
                onPairedMapChange = onShinyViewerPairedMapChange,
                onPlayingChange = { onShinyViewerPlayingChange(it, coroutineScope) },
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
                    bitmapGT = dtuViewerBitmapGT,
                    sceneIndex = dtuViewerSceneIndex,
                    sceneState = dtuViewerSceneState,
                    modifier = Modifier.width(480.dp),
                    onFrameRequest = onDtuViewerFrameRequest,
                    onSceneChange = onDtuViewerSceneChange,
                    portrait = portrait,
                )
                Spacer(Modifier.width(48.dp))
            }

            Row {
                Spacer(Modifier.width(48.dp))
                ShinyViewer(
                    bitmapL = shinyViewerBitmapL,
                    bitmapR = shinyViewerBitmapR,
                    sceneIndex = shinyViewerSceneIndex,
                    sceneState = shinyViewerSceneState,
                    pairedMap = shinyViewerPairedMap,
                    playing = shinyViewerPlaying,
                    portrait = portrait,
                    modifier = Modifier.width(480.dp),
                    onSceneChange = onShinyViewerSceneChange,
                    onPairedMapChange = onShinyViewerPairedMapChange,
                    onPlayingChange = { onShinyViewerPlayingChange(it, coroutineScope) },
                )
                Spacer(Modifier.width(48.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DtuViewer(
    bitmapGT: ImageBitmap?,
    sceneIndex: Int,
    sceneState: AnimatedImageState,
    portrait: Boolean,
    modifier: Modifier = Modifier,
    onFrameRequest: (Int) -> ImageBitmap,
    onSceneChange: (Int) -> Unit,
) {
    val monoFamily = getJetBrainsMonoFamily()
    var openGT by remember { mutableStateOf(false) }

    if (openGT) {
        BasicAlertDialog(onDismissRequest = { openGT = false }) {
            Card(
                colors = CardDefaults.cardColors().copy(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                ) {
                    Box(modifier = Modifier.fillMaxWidth().aspectRatio(1.0f)) {
                        if (sceneState.missing) {
                            // We should never reach this case, otherwise, embrace the glitching UI
                            Icon(
                                imageVector = Icons.Default.Engineering,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp).align(Alignment.Center),
                                tint = LocalContentColor.current.copy(alpha = 0.6f)
                            )
                        } else {
                            AnimatedContent(
                                targetState = bitmapGT,
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.align(Alignment.Center),
                            ) { bitmap ->
                                if (bitmap == null) {
                                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                                } else {
                                    Image(
                                        bitmap = bitmap,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                    )
                                    if (sceneState.loading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(32.dp).align(Alignment.BottomCenter)
                                        )
                                    }
                                }
                            }
                        }

                        IconButton(
                            onClick = { openGT = false },
                            modifier = Modifier.align(Alignment.TopEnd).pointerHoverIcon(PointerIcon.Hand),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Switcher(
                        sceneName = "scan ${DTU_SCENES[sceneIndex]}",
                        onSwitchL = { onSceneChange(if (sceneIndex == 0) DTU_SCENES.lastIndex else sceneIndex - 1) },
                        onSwitchR = { onSceneChange((sceneIndex + 1) % DTU_SCENES.size) },
                        fontFamily = monoFamily,
                        portrait = true,
                    )
                }
            }
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(1.0f)) {
            AnimatedImage(
                modifier = Modifier.align(Alignment.Center),
                loading = sceneState.loading,
                missing = sceneState.missing,
                contentDescription = null,
                onFrameRequest = onFrameRequest,
            )

            IconButton(
                modifier = Modifier
                    .align(if (!portrait) Alignment.TopStart else Alignment.BottomEnd)
                    .pointerHoverIcon(PointerIcon.Hand),
                onClick = { openGT = true },
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        @Suppress("HttpUrlsUsage")
        HyperlinkText(
            linkText = "DTU",
            linkUrl = "http://roboimagedata.compute.dtu.dk/?page_id=36",
            prefix = "Reconstruction on the ",
            suffix = " dataset",
        )

        Spacer(Modifier.height(16.dp))

        Switcher(
            sceneName = "scan ${DTU_SCENES[sceneIndex]}",
            onSwitchL = { onSceneChange(if (sceneIndex == 0) DTU_SCENES.lastIndex else sceneIndex - 1) },
            onSwitchR = { onSceneChange((sceneIndex + 1) % DTU_SCENES.size) },
            fontFamily = monoFamily,
            portrait = portrait,
        )
    }
}

@Composable
private fun ShinyViewer(
    bitmapL: ImageBitmap?,
    bitmapR: ImageBitmap?,
    sceneIndex: Int,
    sceneState: AnimatedImageState,
    pairedMap: DecompositionMap,
    playing: Boolean,
    portrait: Boolean,
    modifier: Modifier = Modifier,
    onSceneChange: (Int) -> Unit,
    onPairedMapChange: (DecompositionMap) -> Unit,
    onPlayingChange: (Boolean) -> Unit,
) {
    val monoFamily = getJetBrainsMonoFamily()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(1.0f)) {
            ImagePair(
                modifier = Modifier.padding(32.dp).align(Alignment.Center),
                bitmapL = bitmapL,
                bitmapR = bitmapR,
                loading = sceneState.loading,
                missing = sceneState.missing,
                contentDescriptionL = null,
                contentDescriptionR = null,
            )

            IconButton(
                modifier = Modifier.align(Alignment.TopEnd).pointerHoverIcon(PointerIcon.Hand),
                onClick = { onPlayingChange(!playing) },
            ) {
                Icon(
                    imageVector = if (playing) Icons.Filled.PauseCircle else Icons.Filled.PlayCircle,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                )
            }

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

        HyperlinkText(
            linkText = "ShinyBlender",
            linkUrl = "https://dorverbin.github.io/refnerf/",
            prefix = if (!portrait) "Decomposition on the " else "Decomposition on ",
            suffix = if (!portrait) " dataset" else "",
        )

        Spacer(Modifier.height(16.dp))

        Switcher(
            sceneName = SHINY_SCENES[sceneIndex],
            onSwitchL = { onSceneChange(if (sceneIndex == 0) SHINY_SCENES.lastIndex else sceneIndex - 1) },
            onSwitchR = { onSceneChange((sceneIndex + 1) % SHINY_SCENES.size) },
            fontFamily = monoFamily,
            portrait = portrait,
        )
    }
}