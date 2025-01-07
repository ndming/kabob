package com.ndming.kabob

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import com.ndming.kabob.fourierseries.generated.resources.Res
import com.ndming.kabob.fourierseries.generated.resources.fs_top_bar_title
import com.ndming.kabob.graphics.*
import com.ndming.kabob.theme.Profile
import com.ndming.kabob.ui.*
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.compose.resources.stringResource
import kotlin.math.PI

private const val VIEWPORT_HALF_EXTENT = FourierSeriesViewModel.CONTENT_HALF_EXTENT + 2.0f
private const val ZOOM_SENSITIVITY = 1.06f

/**
 * A Marker represents a fading segment traced out at the tip of the last arrow.
 */
private data class Marker(
    val offset: Offset,
    val alpha: Float,
)

/**
 * Composable function to render the user interface for visualizing a Fourier series.
 *
 * This function displays an interactive and dynamic Fourier series visualization, including controls
 * to manipulate parameters such as time, zoom, arrows, fading factor, and more. It also allows for 
 * interaction and customization through gestures and UI elements like scrolls and button clicks.
 *
 * @param uiState An object representing the UI state, including currentDrawable, loading status, 
 *                sampling rate, playback state, and other key properties.
 * @param currentTime The current time used for rendering the Fourier series dynamics.
 * @param arrowStates A list of offsets representing the arrow states used in the Fourier series visualization.
 * @param modifier An optional [Modifier] for applying layouts and decorations (default is [Modifier]).
 * @param onProfileChange Callback for when the user changes the profile.
 * @param onTimeChange Callback for when the time changes, providing the new time and a [CoroutineScope].
 * @param onPlay Callback invoked when playback starts, providing a [CoroutineScope].
 * @param onPause Callback invoked when playback pauses, providing a [CoroutineScope].
 * @param onDurationScaleChange Callback for updating the duration scaling factor, accepting a new value 
 *                              and a [CoroutineScope].
 * @param onLockToPathChange Callback for toggling path-locking functionality, receiving a boolean value 
 *                            for the lock state and a [CoroutineScope].
 * @param onAddArrow Callback invoked to add a new arrow to the Fourier series visualization.
 * @param onDropArrow Callback invoked to remove an arrow from the Fourier series visualization.
 * @param onDrawableChange Callback triggered when the drawable changes, receiving the new drawable index 
 *                          and a [CoroutineScope].
 * @param onFadingFactorChange Callback for updating the fading factor used in the visualization.
 * @param onZoomFactor Callback to update the zoom factor for the Fourier series.
 * @param onSamplingRate Callback invoked when the sampling rate changes.
 *
 * @see [FourierSeriesUiState]
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FourierSeriesPage(
    uiState: FourierSeriesUiState,
    currentTime: Float,
    arrowStates: List<Offset>,
    modifier: Modifier = Modifier,
    onProfileChange: (Profile) -> Unit = {},
    onTimeChange: (Float, CoroutineScope) -> Unit = { _, _ -> },
    onPlay: (CoroutineScope) -> Unit = {},
    onPause: (CoroutineScope) -> Unit = {},
    onDurationScaleChange: (Float, CoroutineScope) -> Unit = { _, _ -> },
    onLockToPathChange: (Boolean, CoroutineScope) -> Unit = { _, _ -> },
    onAddArrow: () -> Unit = {},
    onDropArrow: () -> Unit = {},
    onDrawableChange: (Int, CoroutineScope) -> Unit = { _, _ -> },
    onFadingFactorChange: (Float) -> Unit = {},
    onZoomFactor: (Float) -> Unit = {},
    onSamplingRate: (Float) -> Unit = {},
) {
    val arrowColor = MaterialTheme.colorScheme.secondary
    val segmentColor = MaterialTheme.colorScheme.tertiary

    val scope = rememberCoroutineScope()

    val markers = remember { mutableStateListOf<Marker>() }

    var showPortraitDrawableViewer by remember { mutableStateOf(false) }

    Surface(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            KabobTopBar(
                title = stringResource(Res.string.fs_top_bar_title),
                onProfileChange = onProfileChange,
            )

            // Main content
            BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(bottom = 12.dp)) {
                val portrait = maxWidth / maxHeight < 1.8f

                // Close the portrait drawable viewer when switching to non-portrait mode
                LaunchedEffect(portrait) {
                    if (!portrait && showPortraitDrawableViewer) {
                        showPortraitDrawableViewer = false
                    }
                }

                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    if (!portrait) {
                        DrawableViewer(
                            modifier = Modifier.width(300.dp),
                            drawables = DrawableBundle.entries.map { it.drawable },
                            displayNames = DrawableBundle.entries.map { it.displayName },
                            currentDrawableIndex = uiState.currentDrawable,
                            currentSamplingRate = uiState.samplingRate,
                            onSamplingRateChange = onSamplingRate,
                        ) {
                            onDrawableChange(it, scope)
                            markers.clear()
                        }

                        Spacer(Modifier.width(24.dp))
                    }

                    this@BoxWithConstraints.FourierSeriesFrame(portrait) {
                        FourierSeriesOverlay(
                            loading = uiState.loading,
                            playing = uiState.playing,
                            durationScale = uiState.periodSpeed,
                            lockToPath = uiState.lockFactor > 0.0f,
                            arrowCount = uiState.arrowCount,
                            currentTime = currentTime,
                            portrait = portrait,
                            onPlayingChange = { playing -> if (playing) onPlay(scope) else onPause(scope) },
                            onDurationScale = { onDurationScaleChange(it, scope) },
                            onAddArrow = onAddArrow,
                            onDropArrow = onDropArrow,
                            onLockToPath = { onLockToPathChange(it, scope) },
                            onTimeChange = { onTimeChange(it, scope) },
                            onTimeChangeFinished = { markers.clear() },
                            onIncreaseFadingFactor = { onFadingFactorChange(uiState.fadingScale * 1.2f) },
                            onDecreaseFadingFactor = { onFadingFactorChange(uiState.fadingScale / 1.2f) },
                            onPortraitDrawableViewer = { showPortraitDrawableViewer = true },
                            modifier = Modifier
                                .padding(16.dp)
                                .drawBehind {
                                    drawFourierScene(
                                        playing = uiState.playing,
                                        currentTime = currentTime,
                                        durationScale = uiState.periodSpeed,
                                        fadingFactor = uiState.fadingScale,
                                        lockFactor = uiState.lockFactor,
                                        zoomFactor = uiState.zoomFactor,
                                        arrowStates = arrowStates,
                                        markers = markers,
                                        arrowColor = arrowColor,
                                        segmentColor = segmentColor,
                                    )
                                }
                                .onPointerEvent(PointerEventType.Scroll) { event ->
                                    val scrollDelta = event.changes.first().scrollDelta.y
                                    if (scrollDelta != 0.0f) {
                                        onLockToPathChange(true, scope)  // always lock to path when zoom
                                        val newZoom = if (scrollDelta > 0) uiState.zoomFactor / ZOOM_SENSITIVITY
                                        else uiState.zoomFactor * ZOOM_SENSITIVITY
                                        onZoomFactor(newZoom)
                                    }
                                }
                        )
                    }

                    if (!portrait) {
                        Spacer(Modifier.width(24.dp))
                        ComponentViewer(
                            playing = uiState.playing,
                            currentTime = currentTime,
                            modifier = Modifier.width(300.dp)
                        )
                    }
                }

                // Drawable viewer in portrait mode
                this@Column.AnimatedVisibility(
                    modifier = Modifier.align(Alignment.Center),
                    visible = showPortraitDrawableViewer,
                    enter = fadeIn(tween(400)),
                    exit = fadeOut(tween(400)),
                ) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        DrawableViewer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp),
                            drawables = DrawableBundle.entries.map { it.drawable },
                            displayNames = DrawableBundle.entries.map { it.displayName },
                            currentDrawableIndex = uiState.currentDrawable,
                            currentSamplingRate = uiState.samplingRate,
                            portrait = portrait,
                            onSamplingRateChange = onSamplingRate,
                            onPortraitViewerEscape = { showPortraitDrawableViewer = false },
                        ) {
                            onDrawableChange(it, scope)
                            markers.clear()
                            showPortraitDrawableViewer = false
                        }
                    }
                }

                this@Column.AnimatedVisibility(
                    modifier = Modifier.align(Alignment.TopCenter),
                    visible = uiState.loading,
                    enter = fadeIn(tween(200)),
                    exit = fadeOut(tween(200)),
                ) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun BoxWithConstraintsScope.FourierSeriesFrame(
    portrait: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (portrait) {
        content()
    } else {
        Surface(
            modifier = modifier
                .height(maxHeight * 0.9f)
                .aspectRatio(1.0f),
            shape = MaterialTheme.shapes.large,
            border = BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)),
        ) {
            content()
        }
    }
}

private data class Snapshot(
    val origin: Offset,
    val length: Float,
    val radians: Float,
)

private fun DrawScope.drawFourierScene(
    playing: Boolean,
    currentTime: Float,
    durationScale: Float,
    fadingFactor: Float,
    lockFactor: Float,
    zoomFactor: Float,
    arrowStates: List<Offset>,
    markers: MutableList<Marker>,
    arrowColor: Color,
    segmentColor: Color,
) {
    val (snapshots, lastOffset) = arrowStates.toSnapshotsAt(currentTime)
    val translateVector = lastOffset * lockFactor

    // Only add new markers when playing to avoid visual clutter when adding arrows on pause
    // We don't need to trace markers when there's less than 2 arrows, the first dynamic happens at the 2nd arrow
    if (playing && arrowStates.size > 1) markers.add(Marker(lastOffset, 1.0f))

    // Draw tracing markers first so that they stay behind the arrows
    // An iteration draws a segment using markers (i - 1, i) and updates marker i - 1's alpha
    var i = 1
    while (i < markers.size) {
        val currentAlpha = markers[i - 1].alpha
        drawSegment(color = segmentColor) {
            start = markers[i - 1].offset
            end   = markers[i].offset
            alpha = currentAlpha
            width = 3.0f
            translate(-translateVector)
            toViewport(size, VIEWPORT_HALF_EXTENT / zoomFactor)
        }
        val alphaDecrease = fadingFactor * durationScale
        markers[i - 1] = markers[i - 1].copy(alpha = currentAlpha - alphaDecrease)
        ++i
    }

    // We don't want to throttle our memory
    while (markers.isNotEmpty() && markers[0].alpha <= 0.0f) {
        markers.removeAt(0)
    }

    // Draw a dot at the origin
    drawCircle(
        color = arrowColor,
        radius = 5.0f,
        center = (Offset.Zero - translateVector).viewport(size, VIEWPORT_HALF_EXTENT / zoomFactor),
    )

    // Draw rotating arrows
    snapshots.forEach { (origin, length, radians) ->
        drawArrow(color = arrowColor) {
            this.origin = origin
            this.length = length
            rotate(radians)
            translate(-translateVector)
            toViewport(size, VIEWPORT_HALF_EXTENT / zoomFactor)
        }
    }
}

private fun List<Offset>.toSnapshotsAt(time: Float): Pair<List<Snapshot>, Offset> {
    var lastOffset = Offset.Zero
    val snapshots  = ArrayList<Snapshot>(size)

    forEachIndexed { index, (length, theta) ->
        val f = if (index % 2 == 0) index / 2 else -(index + 1) / 2
        val radians = theta + time * f * 2.0f * PI.toFloat()

        snapshots.add(Snapshot(lastOffset, length, radians))

        lastOffset += Offset(1.0f, 0.0f).scale(length).rotate(radians)
    }
    return snapshots to lastOffset
}
