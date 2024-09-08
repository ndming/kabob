package com.ndming.kabob

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ndming.kabob.fourierseries.generated.resources.Res
import com.ndming.kabob.fourierseries.generated.resources.fs_top_bar_title
import com.ndming.kabob.theme.LocalKabobTheme
import com.ndming.kabob.theme.Profile
import com.ndming.kabob.theme.getJetBrainsMonoFamily
import com.ndming.kabob.ui.*
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.compose.resources.stringResource
import kotlin.math.PI

private const val HALF_EXTENT = FourierSeriesViewModel.VIEWPORT_HALF_EXTENT + 2.0f
private const val ZOOM_SENSITIVITY = 1.06f

private data class Marker(
    val offset: Offset,
    val alpha: Float,
)

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
                title = {
                    Text(
                        text = stringResource(Res.string.fs_top_bar_title),
                        fontFamily = getJetBrainsMonoFamily(),
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        maxLines = 1,
                    )
                },
                currentProfile = LocalKabobTheme.current.profile,
                onProfileChange = onProfileChange,
                navigationIcon = {
                    IconButton(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .pointerHoverIcon(PointerIcon.Hand),
                        onClick = { window.open("https://ndming.github.io/", "_self") },
                    ) {
                        Icon(Icons.Default.Home, contentDescription = null)
                    }
                }
            )

            // Main content
            BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(bottom = 12.dp)) {
                val portrait = maxWidth / maxHeight < 1.8f

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
                            currentDrawableIndex = uiState.currentDrawableIndex,
                            currentSamplingRate = uiState.samplingRate,
                            onSamplingRateChange = onSamplingRate,
                            onDrawableSelect = {
                                onDrawableChange(it, scope)
                                markers.clear()
                            },
                        )

                        Spacer(Modifier.width(24.dp))
                    }

                    this@BoxWithConstraints.FourierSeriesFrame(portrait) {
                        FourierSeriesOverlay(
                            loading = uiState.loading,
                            playing = uiState.playing,
                            durationScale = uiState.durationScale,
                            lockToPath = uiState.lockToPath,
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
                            onIncreaseFadingFactor = { onFadingFactorChange(uiState.fadingFactor * 1.2f) },
                            onDecreaseFadingFactor = { onFadingFactorChange(uiState.fadingFactor / 1.2f) },
                            onPortraitDrawableViewer = { showPortraitDrawableViewer = true },
                            modifier = Modifier
                                .padding(12.dp)
                                .drawBehind {
                                    drawFourierScene(
                                        playing = uiState.playing,
                                        currentTime = currentTime,
                                        durationScale = uiState.durationScale,
                                        lockToPath = uiState.lockToPath,
                                        fadingFactor = uiState.fadingFactor,
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
                        FourierSeriesComponentViewer(Modifier.width(300.dp))
                    }
                }

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
                                .padding(horizontal = 12.dp),
                            drawables = DrawableBundle.entries.map { it.drawable },
                            displayNames = DrawableBundle.entries.map { it.displayName },
                            currentDrawableIndex = uiState.currentDrawableIndex,
                            currentSamplingRate = uiState.samplingRate,
                            portrait = portrait,
                            onSamplingRateChange = onSamplingRate,
                            onPortraitViewerEscape = { showPortraitDrawableViewer = false },
                            onDrawableSelect = {
                                onDrawableChange(it, scope)
                                markers.clear()
                                showPortraitDrawableViewer = false
                            },
                        )
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

private data class ArrowInfo(
    val origin: Offset,
    val length: Float,
    val radians: Float,
)

private fun List<Offset>.arrowInfos(time: Float): Pair<List<ArrowInfo>, Offset> {
    var lastOffset = Offset.Zero
    val infos = ArrayList<ArrowInfo>()

    forEachIndexed { index, (length, theta) ->
        val f = if (index % 2 == 0) index / 2 else -(index + 1) / 2
        val radians = theta + time * f * 2.0f * PI.toFloat()

        infos.add(ArrowInfo(lastOffset, length, radians))

        lastOffset += Offset(1.0f, 0.0f).scale(length).rotate(radians)
    }
    return infos to lastOffset
}

private fun DrawScope.drawFourierScene(
    playing: Boolean,
    currentTime: Float,
    durationScale: Float,
    lockToPath: Boolean,
    fadingFactor: Float,
    zoomFactor: Float,
    arrowStates: List<Offset>,
    markers: MutableList<Marker>,
    arrowColor: Color,
    segmentColor: Color,
) {
    val (arrowInfos, lastOffset) = arrowStates.arrowInfos(currentTime)
    val translateVector = if (!lockToPath) Offset.Zero else lastOffset

    // Only add new markers when playing to avoid visual clutter when adding arrows on pause
    if (playing) markers.add(Marker(lastOffset, 1.0f))

    // Draw tracing markers first so that they stay behind the arrows
    var i = 1
    while (i < markers.size) {
        val currentAlpha = markers[i - 1].alpha
        drawSegment(color = segmentColor) {
            start = markers[i - 1].offset
            end = markers[i].offset
            alpha = currentAlpha
            width = 3.0f
            translate(-translateVector)
            mapDrawSpace(size, HALF_EXTENT / zoomFactor)
        }
        val alphaDecrease = when (durationScale) {
            0.25f -> fadingFactor * durationScale * 2.0f
            0.5f -> fadingFactor * durationScale * 1.3f
            else -> fadingFactor * durationScale
        }
        markers[i - 1] = markers[i - 1].copy(alpha = currentAlpha - alphaDecrease)
        ++i
    }

    // We don't want to throttle our memory
    while (markers.isNotEmpty() && markers[0].alpha <= 0.0f) {
        markers.removeAt(0)
    }

    // Draw rotating arrows
    arrowInfos.forEach { (origin, length, radians) ->
        drawArrow(color = arrowColor) {
            this.origin = origin
            this.length = length
            rotate(radians)
            translate(-translateVector)
            mapDrawSpace(size, HALF_EXTENT / zoomFactor)
        }
    }
}
