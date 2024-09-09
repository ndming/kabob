package com.ndming.kabob.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.SwitchLeft
import androidx.compose.material.icons.filled.SwitchRight
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DrawableViewer(
    drawables: List<DrawableResource>,
    displayNames: List<StringResource>,
    currentDrawableIndex: Int,
    currentSamplingRate: Float,
    modifier: Modifier = Modifier,
    portrait: Boolean = false,
    onPortraitViewerEscape: () -> Unit = {},
    onDrawableSelect: (Int) -> Unit,
    onSamplingRateChange: (Float) -> Unit,
) {
    var currentShowingIndex by remember { mutableStateOf(currentDrawableIndex) }

    var expandApplyButton by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if (portrait) {
                IconButton(
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand).padding(vertical = 8.dp),
                    onClick = onPortraitViewerEscape,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = null,
                    )
                }
            } else {
                IconButton(
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand).padding(vertical = 8.dp),
                    onClick = { /* TODO: Add information panel */ },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primaryContainer,
                    )
                }
            }

            AnimatedVisibility(
                enter = fadeIn(tween(400)) + slideInHorizontally (tween(300)) { it },
                exit = fadeOut(tween(300)) + slideOutHorizontally(tween(400)) { it },
                visible = currentShowingIndex != currentDrawableIndex,
            ) {
                Button(
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .pointerHoverIcon(PointerIcon.Hand)
                        .onPointerEvent(PointerEventType.Enter) { expandApplyButton = true }
                        .onPointerEvent(PointerEventType.Exit) { expandApplyButton = false },
                    onClick = { onDrawableSelect(currentShowingIndex) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    ),
                ) {
                    AnimatedVisibility(visible = expandApplyButton) {
                        Text(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            text = "Draw",
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .widthIn(256.dp, 512.dp)
                .aspectRatio(1.0f)
        ) {
            AnimatedContent(
                modifier = Modifier.align(Alignment.Center).padding(24.dp),
                targetState = currentShowingIndex,
                contentAlignment = Alignment.Center,
            ) { index ->
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(drawables[index]),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(
                        color = if (index == currentDrawableIndex) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.onTertiaryContainer
                    )
                )
            }
        }

        DrawableIndicator(
            modifier = Modifier.padding(vertical = 12.dp),
            drawableCount = drawables.size,
            currentDrawableIndex = currentShowingIndex,
        )

        DrawableSwitcher(
            drawableName = stringResource(displayNames[currentShowingIndex]),
            onStepForward = { currentShowingIndex = (currentShowingIndex + 1) % displayNames.size },
            onStepBackward = { if (currentShowingIndex > 0) currentShowingIndex -= 1 else currentShowingIndex = displayNames.size - 1 }
        )

        SamplingRateOption(
            modifier = Modifier.padding(horizontal = 8.dp).padding(top = 24.dp, bottom = 12.dp),
            currentDrawableIndex = currentDrawableIndex,
            currentShowingIndex = currentShowingIndex,
            currentSamplingRate = currentSamplingRate,
            onSamplingRateChange = onSamplingRateChange,
        )
    }
}


@Composable
private fun DrawableIndicator(
    drawableCount: Int,
    currentDrawableIndex: Int,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        repeat(drawableCount) { index ->
            Surface(
                modifier = Modifier.size(8.dp).aspectRatio(1.0f),
                shape = CircleShape,
                color = if (index == currentDrawableIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
            ) {}
            if (index < drawableCount - 1) {
                Spacer(modifier = Modifier.padding(4.dp))
            }
        }
    }
}

@Composable
private fun DrawableSwitcher(
    drawableName: String,
    modifier: Modifier = Modifier,
    onStepForward: () -> Unit,
    onStepBackward: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
            onClick = onStepBackward,
        ) {
            Icon(Icons.Default.SwitchLeft,null)
        }

        AnimatedContent(
            targetState = drawableName,
            contentAlignment = Alignment.Center,
        ) { name ->
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
        }

        IconButton(
            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
            onClick = onStepForward,
        ) {
            Icon(Icons.Default.SwitchRight,null)
        }
    }
}

@Composable
private fun SamplingRateOption(
    currentSamplingRate: Float,
    currentShowingIndex: Int,
    currentDrawableIndex: Int,
    modifier: Modifier = Modifier,
    onSamplingRateChange: (Float) -> Unit,
) {
    val activated = currentShowingIndex != currentDrawableIndex

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Sampling Rate",
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.width(16.dp))

        Slider(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .pointerHoverIcon(PointerIcon.Hand),
            enabled = activated,
            value = currentSamplingRate,
            onValueChange = onSamplingRateChange,
            steps = 3,
            valueRange = 25.0f..225.0f,
        )
    }
}
