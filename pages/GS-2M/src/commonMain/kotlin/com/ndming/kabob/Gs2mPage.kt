package com.ndming.kabob

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import com.ndming.kabob.markup.HyperlinkText
import com.ndming.kabob.theme.Profile
import com.ndming.kabob.ui.*

@Composable
fun Gs2mPage(
    uiState: Gs2mUiState,
    modifier: Modifier = Modifier,
    onProfileChange: (Profile) -> Unit = {},
    onDtuViewerFrameRequest: (Int) -> ImageBitmap,
    onDtuViewerSceneChange: (Int) -> Unit,
    onShinyViewerFrameRequest: (Int) -> Pair<ImageBitmap, ImageBitmap>,
    onShinyViewerSceneChange: (Int) -> Unit,
    onShinyViewerPairedMapChange: (DecompositionMap) -> Unit,
    onShinyMeshFrameRequest: (Int) -> Triple<ImageBitmap, ImageBitmap, ImageBitmap>,
    onShinyMeshSceneChange: (Int) -> Unit,
    onShinyMeshPairedMethodChange: (ReconstructionMethod) -> Unit,
) {
    val scrollState = rememberScrollState()

    Surface(modifier = modifier.fillMaxSize()) {
        SelectionContainer {
            Column(modifier = Modifier.fillMaxSize()) {
                KabobTopBar(
                    title = if (scrollState.value > 0) "GS-2M" else "",
                    onProfileChange = onProfileChange,
                )

                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val portrait = maxWidth / maxHeight < 1.0f

                    Column(
                        modifier = Modifier.fillMaxWidth().verticalScroll(scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        PaperBanner()

                        PaperCover(
                            dtuViewerSceneIndex = uiState.dtuViewerSceneIndex,
                            dtuViewerSceneState = uiState.dtuViewerSceneState,
                            shinyViewerSceneIndex = uiState.shinyViewerSceneIndex,
                            shinyViewerSceneState = uiState.shinyViewerSceneState,
                            shinyViewerPairedMap = uiState.shinyViewerPairedMap,
                            portrait = portrait,
                            onDtuViewerFrameRequest = onDtuViewerFrameRequest,
                            onDtuViewerSceneChange = onDtuViewerSceneChange,
                            onShinyViewerFrameRequest = onShinyViewerFrameRequest,
                            onShinyViewerSceneChange = onShinyViewerSceneChange,
                            onShinyViewerPairedMapChange = onShinyViewerPairedMapChange,
                        )

                        Spacer(modifier = Modifier.height(56.dp))

                        PaperAbstract()

                        Spacer(modifier = Modifier.height(48.dp))

                        if (portrait) {
                            Text(
                                text = "Compare GS-2M and SoTA methods on"
                            )

                            HyperlinkText(
                                linkText = "ShinyBlender",
                                linkUrl = "https://dorverbin.github.io/refnerf/",
                                prefix = "reflective surfaces (",
                                suffix = ").",
                            )
                        } else {
                            HyperlinkText(
                                linkText = "ShinyBlender",
                                linkUrl = "https://dorverbin.github.io/refnerf/",
                                prefix = "Compare the reconstructed meshes of GS-2M and SoTA methods. Dataset:  ",
                                suffix = " synthetic.",
                            )

                            @Suppress("HttpUrlsUsage")
                            HyperlinkText(
                                linkText = "DTU",
                                linkUrl = "http://roboimagedata.compute.dtu.dk/?page_id=36",
                                prefix = "Our method faithfully handles reflective surfaces, while maintaining the reconstruction quality on the  ",
                                suffix = " dataset.",
                            )
                        }

                        ShinyMeshComparison(
                            sceneIndex = uiState.shinyMeshSceneIndex,
                            sceneState = uiState.shinyMeshSceneState,
                            pairedMethod = uiState.shinyMeshPairedMethod,
                            portrait = portrait,
                            onFrameRequest = onShinyMeshFrameRequest,
                            onSceneChange = onShinyMeshSceneChange,
                            onPairedMethodChange = onShinyMeshPairedMethodChange,
                        )

                        Spacer(modifier = Modifier.height(48.dp))
                    }
                }
            }
        }
    }
}