package com.ndming.kabob.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import com.ndming.kabob.fourierseries.generated.resources.Res
import com.ndming.kabob.fourierseries.generated.resources.avg_pace
import org.jetbrains.compose.resources.painterResource

@Composable
fun FourierSeriesOverlay(
    loading: Boolean,
    playing: Boolean,
    durationScale: Float,
    lockToPath: Boolean,
    arrowCount: Int,
    currentTime: Float,
    modifier: Modifier = Modifier,
    portrait: Boolean,
    onPlayingChange: (Boolean) -> Unit = {},
    onDurationScale: (Float) -> Unit = {},
    onLockToPath: (Boolean) -> Unit = {},
    onAddArrow: () -> Unit = {},
    onDropArrow: () -> Unit = {},
    onTimeChange: (Float) -> Unit = {},
    onTimeChangeFinished: () -> Unit = {},
    onIncreaseFadingFactor: () -> Unit = {},
    onDecreaseFadingFactor: () -> Unit = {},
    onPortraitDrawableViewer: () -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row {
                Column {
                    // Lock-to-path button
                    FilledIconToggleButton(
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                        checked = lockToPath,
                        onCheckedChange = onLockToPath,
                    ) {
                        Icon(
                            imageVector = if (lockToPath) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = null,
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    DurationPanel(
                        loading = loading,
                        scale = durationScale,
                        onScale = onDurationScale,
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    FadingFactorPanel(
                        onIncreaseFadingFactor = onIncreaseFadingFactor,
                        onDecreaseFadingFactor = onDecreaseFadingFactor,
                    )
                }

                if (portrait) {
                    FilledTonalButton(
                        modifier = Modifier.padding(horizontal = 12.dp).pointerHoverIcon(PointerIcon.Hand),
                        onClick = onPortraitDrawableViewer,
                    ) {
                        Text(text = "More")
                    }
                }
            }

            ArrowCountPanel(
                arrowCount = arrowCount,
                loading = loading,
                onAddArrow = onAddArrow,
                onDropArrow = onDropArrow,
            )
        }

        Row {
            // Play/pause button
            IconButton(
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                enabled = !loading,
                onClick = { onPlayingChange(!playing) },
            ) {
                Icon(
                    imageVector = if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                )
            }

            // Time slider
            Slider(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .pointerHoverIcon(PointerIcon.Hand),
                enabled = !playing && !loading,
                value = currentTime,
                onValueChange = onTimeChange,
                onValueChangeFinished = onTimeChangeFinished,
            )
        }
    }
}

@Composable
private fun DurationPanel(
    loading: Boolean,
    scale: Float,
    modifier: Modifier = Modifier,
    onScale: (Float) -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IconButton(
            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
            enabled = !loading,
            onClick = {
                when (scale) {
                    0.25f -> onScale(0.5f)
                    0.5f -> onScale(1f)
                    1f -> onScale(2f)
                }
            },
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDropUp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        Text(
            text = "x$scale",
            style = MaterialTheme.typography.labelLarge,
        )

        IconButton(
            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
            enabled = !loading,
            onClick = {
                when (scale) {
                    2f -> onScale(1f)
                    1f -> onScale(0.5f)
                    0.5f -> onScale(0.25f)
                }
            },
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun FadingFactorPanel(
    modifier: Modifier = Modifier,
    onIncreaseFadingFactor: () -> Unit,
    onDecreaseFadingFactor: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IconButton(
            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
            onClick = onIncreaseFadingFactor,
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDropUp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        Icon(
            painter = painterResource(Res.drawable.avg_pace),
            contentDescription = null,
        )

        IconButton(
            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
            onClick = onDecreaseFadingFactor,
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun ArrowCountPanel(
    arrowCount: Int,
    loading: Boolean,
    modifier: Modifier = Modifier,
    onAddArrow: () -> Unit,
    onDropArrow: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FilledIconButton(
            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
            enabled = !loading,
            onClick = onAddArrow,
        ) {
            Icon(Icons.Default.KeyboardArrowUp, null)
        }

        Text(
            modifier = Modifier.padding(vertical = 12.dp),
            text = arrowCount.toString(),
            style = MaterialTheme.typography.titleMedium,
        )

        FilledIconButton(
            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
            enabled = !loading,
            onClick = onDropArrow,
        ) {
            Icon(Icons.Default.KeyboardArrowDown, null)
        }
    }
}