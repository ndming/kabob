package com.ndming.kabob.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwitchLeft
import androidx.compose.material.icons.filled.SwitchRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ndming.kabob.markup.HyperlinkText
import com.ndming.kabob.media.AnimatedImage
import com.ndming.kabob.theme.getJetBrainsMonoFamily

private val DTU_SCENES = listOf(24, 37, 40, 55, 63, 65, 69, 83, 97, 105, 106, 110, 114, 118, 122)
private val SHINY_SCENES = listOf("car", "coffee", "helmet", "teapot")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PaperCover(
    portrait: Boolean,
    modifier: Modifier = Modifier,
) {
    if (portrait) {
        Column(
            modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DtuViewer(modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.height(56.dp))

            DtuViewer(modifier = Modifier.fillMaxWidth())
        }
    } else {
        FlowRow(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            DtuViewer(modifier = Modifier.width(460.dp).padding(end = 24.dp))

            DtuViewer(modifier = Modifier.width(460.dp).padding(start = 24.dp))
        }
    }
}

@Composable
private fun DtuViewer(modifier: Modifier) {
    var sceneIndex by remember { mutableStateOf(0) }
    val monoFamily = getJetBrainsMonoFamily()

    Box(modifier = modifier.fillMaxWidth().aspectRatio(0.9f)) {
        @Suppress("HttpUrlsUsage")
        HyperlinkText(
            linkText = "DTU",
            linkUrl = "http://roboimagedata.compute.dtu.dk/?page_id=36",
            prefix = "Reconstruction on the ",
            suffix = " dataset",
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 32.dp),
        )

        AnimatedContent(
            modifier = modifier.align(Alignment.Center),
            targetState = sceneIndex,
            contentAlignment = Alignment.Center
        ) { index ->
            AnimatedImage(filePath = "files/dtu/scan${DTU_SCENES[index]}/anim.webp")
        }

        Row(
            modifier = modifier.fillMaxWidth().align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Spacer(Modifier.width(32.dp))
                FilledTonalButton(
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                    onClick = { sceneIndex = if (sceneIndex == 0) DTU_SCENES.lastIndex else sceneIndex - 1 },
                ) {
                    Icon(Icons.Default.SwitchLeft,null)
                }
            }

            AnimatedContent(
                targetState = sceneIndex,
                contentAlignment = Alignment.CenterStart,
                transitionSpec = {
                    val contentEnter = fadeIn(tween(800)) + slideInVertically(tween(600)) { -it / 2 }
                    val contentExit = fadeOut(tween(200)) + slideOutVertically(tween(400)) { it / 2 }
                    contentEnter.togetherWith(contentExit)
                },
            ) { index ->
                Text(
                    text = "scan ${DTU_SCENES[index]}",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    fontFamily = monoFamily,
                    fontWeight = FontWeight.Medium,
                )
            }

            Row {
                FilledTonalButton(
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                    onClick = { sceneIndex = (sceneIndex + 1) % DTU_SCENES.size },
                ) {
                    Icon(Icons.Default.SwitchRight,null)
                }
                Spacer(Modifier.width(32.dp))
            }
        }
    }
}