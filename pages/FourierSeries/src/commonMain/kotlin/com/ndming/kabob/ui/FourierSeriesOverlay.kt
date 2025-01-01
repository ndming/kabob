package com.ndming.kabob.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import com.ndming.kabob.fourierseries.generated.resources.fs_overlay_add_arrow_tip
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * A composable function that displays the UI elements for controlling and visualizing a Fourier series overlay.
 *
 * @param loading Indicates whether the overlay is in a loading state, disabling certain UI interactions.
 * @param playing Indicates whether the Fourier series animation is currently playing.
 * @param durationScale Represents the scaling factor for animation duration, which can be adjusted using the UI.
 * @param lockToPath A boolean indicating if the animation is locked to the target path.
 * @param arrowCount The number of arrows currently displayed in the overlay, adjustable via UI controls.
 * @param currentTime The current animation time, which can be changed via the time slider.
 * @param modifier The [Modifier] applied to the root column of the overlay.
 * @param portrait A boolean indicating whether the display is in portrait mode, which toggles certain UI elements.
 * @param onPlayingChange A callback triggered when the play/pause state changes; receives the new playing state as an argument.
 * @param onDurationScale A callback invoked when the user adjusts the animation duration scaling factor.
 * @param onLockToPath A callback triggered when the lock-to-path state is toggled.
 * @param onAddArrow A callback triggered when a new arrow is added to the overlay.
 * @param onDropArrow A callback triggered when an arrow is removed from the overlay.
 * @param onTimeChange A callback triggered during animation time changes, typically from the time slider interaction.
 * @param onTimeChangeFinished A callback invoked once the animation time change via the slider is complete.
 * @param onIncreaseFadingFactor A callback invoked when the user increases the fading factor.
 * @param onDecreaseFadingFactor A callback invoked when the user decreases the fading factor.
 * @param onPortraitDrawableViewer A callback triggered when the user clicks the "More" button in portrait mode.
 */
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
                        modifier = Modifier.padding(horizontal = 18.dp).pointerHoverIcon(PointerIcon.Hand),
                        onClick = onPortraitDrawableViewer,
                    ) {
                        Text(text = "More")
                    }
                }
            }

            ArrowCountPanel(
                arrowCount = arrowCount,
                loading = loading,
                playing = playing,
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
    playing: Boolean,
    modifier: Modifier = Modifier,
    onAddArrow: () -> Unit,
    onDropArrow: () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.End) {
        AnimatedVisibility(
            visible = playing && arrowCount == 0,
            enter = fadeIn(tween(400)) + slideInHorizontally (tween(300)) { it },
            exit = fadeOut(tween(300)) + slideOutHorizontally(tween(400)) { it },
        ) {
            Surface(
                modifier = Modifier.wrapContentSize().padding(end = 8.dp, top = 12.dp),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.tertiaryContainer,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        text = stringResource(Res.string.fs_overlay_add_arrow_tip),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )

                    Icon(
                        modifier = Modifier.size(18.dp),
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                    )
                }
            }
        }

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
}
