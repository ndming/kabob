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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ndming.kabob.markup.HyperlinkText
import com.ndming.kabob.media.AnimatedImage
import com.ndming.kabob.media.AnimatedImagePair
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

            ShinyViewer(portrait = true, modifier = Modifier.fillMaxWidth())
        }
    } else {
        FlowRow(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            DtuViewer(modifier = Modifier.width(460.dp).padding(end = 24.dp))

            ShinyViewer(portrait = false, modifier = Modifier.width(460.dp).padding(start = 24.dp))
        }
    }
}

@Composable
private fun DtuViewer(modifier: Modifier = Modifier) {
    var sceneIndex by remember { mutableStateOf(0) }
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

            AnimatedContent(
                modifier = Modifier.align(Alignment.Center),
                targetState = sceneIndex,
                contentAlignment = Alignment.Center
            ) { index ->
                AnimatedImage(filePath = "files/dtu/scan${DTU_SCENES[index]}/anim.webp")
            }
        }

        Spacer(Modifier.height(16.dp))

        Switcher(
            sceneName = "scan ${DTU_SCENES[sceneIndex]}",
            onSwitchL = { sceneIndex = if (sceneIndex == 0) DTU_SCENES.lastIndex else sceneIndex - 1 },
            onSwitchR = { sceneIndex = (sceneIndex + 1) % DTU_SCENES.size },
            fontFamily = monoFamily,
        )
    }
}

@Composable
private fun ShinyViewer(
    portrait: Boolean,
    modifier: Modifier = Modifier,
) {
    var sceneIndex by remember { mutableStateOf(0) }
    val monoFamily = getJetBrainsMonoFamily()

    var fraction by remember { mutableStateOf(0.5f) }

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
                filePathImgL = "files/shiny/${SHINY_SCENES[sceneIndex]}/gt.webp",
                filePathImgR = "files/shiny/${SHINY_SCENES[sceneIndex]}/normal.webp",
                fraction = fraction,
                onFractionChange = { fraction = it },
            )
        }

        if (portrait) {
            Slider(
                value = fraction,
                onValueChange = { fraction = it },
                valueRange = 0.0f..1.0f,
                modifier = Modifier
                    .pointerHoverIcon(PointerIcon.Hand)
                    .padding(horizontal = 32.dp),
            )
        }

        Spacer(Modifier.height(16.dp))

        Switcher(
            sceneName = SHINY_SCENES[sceneIndex],
            onSwitchL = { sceneIndex = if (sceneIndex == 0) DTU_SCENES.lastIndex else sceneIndex - 1 },
            onSwitchR = { sceneIndex = (sceneIndex + 1) % DTU_SCENES.size },
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
            Spacer(Modifier.width(32.dp))
            FilledTonalButton(
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                onClick = onSwitchL,
            ) {
                Icon(Icons.Default.SwitchLeft,null)
            }
        }

        AnimatedContent(
            targetState = sceneName,
            contentAlignment = Alignment.CenterStart,
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
            Spacer(Modifier.width(32.dp))
        }
    }
}