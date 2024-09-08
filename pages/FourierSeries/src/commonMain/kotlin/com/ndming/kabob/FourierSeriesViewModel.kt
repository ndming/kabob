package com.ndming.kabob

import androidx.compose.animation.core.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.viewModelScope
import com.ndming.kabob.fourierseries.generated.resources.Res
import com.ndming.kabob.svg.buildStandardPath
import com.ndming.kabob.svg.parseDrawableSVG
import com.ndming.kabob.svg.sample
import com.ndming.kabob.theme.ThemeAwareViewModel
import com.ndming.kabob.ui.DrawableBundle
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.w3c.dom.get
import kotlin.math.*

data class FourierSeriesUiState(
    val loading: Boolean = false,
    val playing: Boolean = false,
    val durationScale: Float = 1f,
    val lockToPath: Boolean = false,
    val arrowCount: Int = 0,
    val currentDrawableIndex: Int = 0,
    val fadingFactor: Float = 2.5e-3f,
    val zoomFactor: Float = 1.0f,
    val samplingRate: Float = 125.0f,
)

class FourierSeriesViewModel : ThemeAwareViewModel() {
    private val _uiState: MutableStateFlow<FourierSeriesUiState>

    init {
        val drawableIndex = window.sessionStorage[FS_CURRENT_DRAWABLE_KEY]
            ?.toInt()?.takeIf { it > 0 && it < DrawableBundle.entries.size } ?: 0

        _uiState = MutableStateFlow(FourierSeriesUiState(currentDrawableIndex = drawableIndex))
    }

    val uiState: StateFlow<FourierSeriesUiState> = _uiState.asStateFlow()

    private val timeAnimator = Animatable(0.0f)
    val currentTime by timeAnimator.asState()

    private var samples: List<Offset> = listOf()
    private var baseDurationMillis: Float = 0.0f
    private var baseFadingFactor: Float = 2.5e-3f

    init { viewModelScope.launch { updateSampleAt(0) } }

    private val zoomAnimator = Animatable(_uiState.value.zoomFactor)

    private val cycleDurationMillis: Float
        get() = baseDurationMillis / _uiState.value.durationScale

    private val _arrowStates: MutableList<Offset> = mutableListOf()
    val arrowsStates: List<Offset> = _arrowStates

    fun play(scope: CoroutineScope) {
        _uiState.update { it.copy(playing = true) }
        scope.launch { timeAnimator.animatePlay(cycleDurationMillis) }
    }

    fun pause(scope: CoroutineScope) {
        scope.launch { timeAnimator.stop() }
        _uiState.update { it.copy(playing = false) }
    }

    fun changeDurationScale(scale: Float, scope: CoroutineScope) {
        _uiState.update { it.copy(playing = true, durationScale = scale) }
        scope.launch { timeAnimator.animatePlay(cycleDurationMillis) }
    }

    fun changeTime(time: Float, scope: CoroutineScope) {
        scope.launch { timeAnimator.snapTo(time) }
    }

    fun changeLockToPath(locked: Boolean, scope: CoroutineScope) {
        if (!locked && _uiState.value.zoomFactor > 1.5f && !zoomAnimator.isRunning) {
            scope.launch {
                zoomAnimator.snapTo(_uiState.value.zoomFactor)
                zoomAnimator.animateTo(targetValue = 1.0f, animationSpec = tween(600)) {
                    _uiState.update { it.copy(zoomFactor = this.value) }
                }
            }
        } else if (!zoomAnimator.isRunning) {
            _uiState.update { it.copy(lockToPath = locked) }
        }
    }

    fun changeFadingFactor(factor: Float) {
        // Fading factor shouldn't get too small to avoid harming performance
        if (factor > baseFadingFactor / 2.0f) {
            _uiState.update { it.copy(fadingFactor = factor) }
        }
    }

    fun changeZoomFactor(factor: Float) {
        // Only zoom-in is allowed
        if (factor >= 1.0f) {
            _uiState.update { it.copy(zoomFactor = factor) }
        }
    }

    fun changeSamplingRate(rate: Float) {
        _uiState.update { it.copy(samplingRate = rate) }
    }

    fun changeDrawable(index: Int, scope: CoroutineScope) {
        // Reset model states
        _uiState.update { it.copy(loading = true, currentDrawableIndex = index, arrowCount = 0, zoomFactor = 1.0f) }
        pause(scope)
        changeTime(0.0f, scope)
        _arrowStates.clear()

        // Load sample data for the new drawable
        scope.launch {
            updateSampleAt(index)
            _uiState.update { it.copy(loading = false) }
        }

        // Store the current drawable
        window.sessionStorage.setItem(FS_CURRENT_DRAWABLE_KEY, index.toString())
    }

    @OptIn(ExperimentalResourceApi::class)
    private suspend fun updateSampleAt(index: Int) {
        val bytes = Res.readBytes(DrawableBundle.entries[index].path)
        val (viewBox, pathData) = parseDrawableSVG(bytes)
        samples = buildStandardPath(viewBox, pathData, VIEWPORT_HALF_EXTENT).sample(_uiState.value.samplingRate)

        // Balance out the cycle duration and fading rate
        baseDurationMillis = samples.size.toFloat() / 1.5f
        baseFadingFactor = 17.0f / baseDurationMillis
    }

    fun addArrow() {
        val currentArrowCount = _uiState.value.arrowCount
        if (currentArrowCount == _arrowStates.size) {
            increaseArrow()
        }
        _uiState.update { it.copy(arrowCount = currentArrowCount + 1) }
    }

    fun dropArrow() {
        val currentArrowCount = _uiState.value.arrowCount
        val updatedArrowCount = if (currentArrowCount > 0) currentArrowCount - 1 else 0
        _uiState.update { it.copy(arrowCount = updatedArrowCount) }
    }

    private fun increaseArrow() {
        val newArrowIndex = _arrowStates.size

        // Get the frequency of the new arrow, the sequence is: 0, -1, 1, -2, 2, ...
        val f = if (newArrowIndex % 2 == 0) newArrowIndex / 2 else -(newArrowIndex + 1) / 2
        // Get the length and initial theta of this arrow
        val (length, theta) = integrateEstimate(f)
        // Append this state to the arrow state list
        _arrowStates.add(Offset(length, theta))
    }

    private fun integrateEstimate(frequency: Int): Pair<Float, Float> {
        val step = if (samples.size < 2) 0.0f else 1.0f / (samples.size - 1)

        val absN = abs(frequency)

        var pn = 0.0f
        var qn = 0.0f
        var rn = 0.0f
        var sn = 0.0f
        samples.forEachIndexed { idx, (xt, yt) ->
            val cosValue = cos(absN.toFloat() * 2.0f * PI.toFloat() * step * idx.toFloat()) * step
            val sinValue = sin(absN.toFloat() * 2.0f * PI.toFloat() * step * idx.toFloat()) * step
            pn += xt * cosValue
            qn += yt * cosValue
            rn += xt * sinValue
            sn += yt * sinValue
        }

        val (nx, ny) = if (frequency < 0) {
            (pn - sn) to (qn + rn)
        } else {
            (pn + sn) to (qn - rn)
        }

        return sqrt(nx * nx + ny * ny) to atan2(ny, nx)
    }

    companion object {
        const val VIEWPORT_HALF_EXTENT = 10.0f

        private const val FS_CURRENT_DRAWABLE_KEY = "fs_drawable_index"
    }
}

private suspend fun Animatable<Float, AnimationVector1D>.animatePlay(durationMillis: Float) {
    // Animatable retains its state when stopped, we will have to animate it to the full cycle with less duration
    if (value > 0.0f) {
        val duration = (durationMillis * (1.0f - value)).toInt()
        animateTo(1.0f, tween(duration, easing = LinearEasing))
        // This reset the state to 0, otherwise we'll keep sticking at 1
        snapTo(0.0f)
    }
    animateTo(1.0f, infiniteRepeatable(tween(durationMillis.toInt(), easing = LinearEasing)))
}
