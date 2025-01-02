package com.ndming.kabob

import androidx.compose.animation.core.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.viewModelScope
import com.ndming.kabob.fourierseries.generated.resources.Res
import com.ndming.kabob.svg.makePath
import com.ndming.kabob.svg.readPathString
import com.ndming.kabob.svg.sample
import com.ndming.kabob.theme.ThemeAwareViewModel
import com.ndming.kabob.ui.DrawableBundle
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.w3c.dom.events.Event
import org.w3c.dom.get
import kotlin.math.*

/**
 * Represents the state of the Fourier Series UI and its associated properties.
 *
 * @property loading Indicates whether the UI is in a loading state (when loading a new drawable).
 * @property playing Indicates whether the Fourier series animation is currently playing.
 * @property arrowCount The number of currently active arrows in the animation.
 * @property currentDrawable The index of the currently selected drawable resource.
 * @property periodSpeed How much time it will take for the animation to complete a full period.
 * @property fadingScale Controls the fading effect of the path traced by the tip of the last arrow.
 * @property zoomFactor Represents the current zoom factor for the animation view.
 * @property lockFactor Determines if the viewport center is at the origin (0.0f) or locked to the tip of the last arrow (1.0f)
 * @property samplingRate Defines the rate at which the drawable path is sampled.
 */
data class FourierSeriesUiState(
    val loading:         Boolean = true,
    val playing:         Boolean = false,
    val arrowCount:      Int     = 0,
    val currentDrawable: Int     = 0,
    val periodSpeed:     Float   = 1.0f,
    val fadingScale:     Float   = 1.0f,
    val lockFactor:      Float   = 0.0f,
    val zoomFactor:      Float   = 1.0f,
    val samplingRate:    Float   = 125.0f,
)

/**
 * A ViewModel that manages the state and interactions for the Fourier Series animation UI.
 *
 * The class handles the business logic, UI state, and interactions related to the Fourier Series animation.
 * It supports controlling the animation (play, pause, change speed), zooming, drawing paths, sampling rate adjustments, 
 * and managing drawable resources. The ViewModel also ensures the animation adapts to changes in visibility, such as 
 * when the window loses focus.
 *
 * This class is responsible for:
 *  - Managing the current state of the [FourierSeriesUiState].
 *  - Handling animation-related operations like playing, pausing, adjusting speed, and time.
 *  - Loading and sampling drawable paths for the animation.
 *  - Managing zoom, fading, and other visual or performance-based properties.
 *  - Storing and restoring states like the current drawable index in session storage.
 *  
 *  @see [ThemeAwareViewModel]
 */
class FourierSeriesViewModel : ThemeAwareViewModel() {
    private val _uiState: MutableStateFlow<FourierSeriesUiState>

    init {
        // Retrieve previously retained drawable, if any
        val drawableIndex = window.sessionStorage[FS_CURRENT_DRAWABLE_KEY]
            ?.toInt()
            ?.takeIf { it > 0 && it < DrawableBundle.entries.size }
            ?: 0

        _uiState = MutableStateFlow(FourierSeriesUiState(currentDrawable = drawableIndex))
        
        viewModelScope.launch {
            updateSampleAt(drawableIndex)
            _uiState.update { it.copy(loading = false, fadingScale = fadingDuration) }
        }
    }

    /**
     * A [StateFlow] that holds the current UI state for the Fourier Series ViewModel.
     * It represents the state exposed to the UI, which includes properties like
     * loading status, playing status, zoom factor, and more.
     * 
     * @see [FourierSeriesUiState]
     */
    val uiState: StateFlow<FourierSeriesUiState> = _uiState.asStateFlow()

    // Callback for handling changes in document visibility
    private val visibilityChangeCallback: (Event) -> Unit = {
        if (windowHidden() && _uiState.value.playing) {
            // Somehow we can pause with viewModelScope here?
            pause(viewModelScope)
        }
    }

    init {
        // Pause ongoing animation if the window loses focus
        document.addEventListener("visibilitychange", visibilityChangeCallback)
    }

    private val timeAnimator = Animatable(0.0f)

    /**
     * Represents the current time fraction of the Fourier series animation between $[0, 1]$.
     *
     * This value reflects the progression of the animation within the period and can be observed in composition.
     */
    val currentTime by timeAnimator.asState()

    private var periodDuration: Float = 0.0f
    private var fadingDuration: Float = 1.0f

    fun play(scope: CoroutineScope) {
        _uiState.update { it.copy(playing = true) }
        scope.launch { timeAnimator.animatePlay(periodDuration / _uiState.value.periodSpeed) }
    }

    fun pause(scope: CoroutineScope) {
        scope.launch { timeAnimator.stop() }
        _uiState.update { it.copy(playing = false) }
    }

    fun changePeriodSpeed(speed: Float, scope: CoroutineScope) {
        _uiState.update { it.copy(playing = true, periodSpeed = speed) }
        scope.launch { timeAnimator.animatePlay(periodDuration / _uiState.value.periodSpeed) }
    }

    fun changeTime(time: Float, scope: CoroutineScope) {
        scope.launch { timeAnimator.snapTo(time) }
    }

    fun changeFadingFactor(factor: Float) {
        // Fading factor shouldn't get too small to avoid harming performance
        if (factor > fadingDuration / 2.0f) {
            _uiState.update { it.copy(fadingScale = factor) }
        }
    }

    private val lockAnimator = Animatable(_uiState.value.lockFactor)
    private val zoomAnimator = Animatable(_uiState.value.zoomFactor)

    fun changeLockToPath(lock: Boolean, scope: CoroutineScope) {
        scope.launch {
            lockAnimator.animateTo(targetValue = if (lock) 1.0f else 0.0f, animationSpec = tween(600)) {
                _uiState.update { it.copy(lockFactor = this.value) }
            }
        }
        if (!lock) {
            // Also zooming out if changing to lock-free
            scope.launch {
                zoomAnimator.snapTo(_uiState.value.zoomFactor)
                zoomAnimator.animateTo(targetValue = 1.0f, animationSpec = tween(600)) {
                    _uiState.update { it.copy(zoomFactor = this.value) }
                }
            }
        }
    }

    fun changeZoomFactor(factor: Float) {
        // Only zoom-in is allowed
        if (factor >= 1.0f && !zoomAnimator.isRunning) {
            _uiState.update { it.copy(zoomFactor = factor) }
        }
    }

    fun changeSamplingRate(rate: Float) {
        _uiState.update { it.copy(samplingRate = rate) }
    }

    private val _arrowStates: MutableList<Offset> = mutableListOf()

    /**
     * The list of arrow states estimating the current drawable.
     *
     * This property provides a snapshot at time 0 of the arrow states (offsets), which are calculated based on the
     * Fourier coefficients derived from the drawable path's sampled points. These offsets correspond to the
     * magnitude and phase (angle in radians) of each arrow in the Fourier series representation.
     *
     * Note that not all values in the list are necessary for the current drawing. The [FourierSeriesUiState.arrowCount]
     * defining the number of currently active arrows should be used to get the right number of states.
     */
    val arrowsStates: List<Offset> = _arrowStates

    /**
     * A list of sampled points derived from the drawable path.
     *
     * These samples represent points evenly spaced along the drawable path and are used for calculating
     * Fourier coefficients and rendering the Fourier series animation.
     */
    private var samples: List<Offset> = listOf()

    fun changeDrawable(index: Int, scope: CoroutineScope) {
        // Reset model states
        _uiState.update { it.copy(loading = true, currentDrawable = index, arrowCount = 0, zoomFactor = 1.0f) }
        pause(scope)
        changeTime(0.0f, scope)
        _arrowStates.clear()

        // Load sample data for the new drawable
        scope.launch {
            updateSampleAt(index)
            _uiState.update { it.copy(loading = false, fadingScale = fadingDuration) }
        }

        // Store the current drawable index to session storage
        window.sessionStorage.setItem(FS_CURRENT_DRAWABLE_KEY, index.toString())
    }

    @OptIn(ExperimentalResourceApi::class)
    private suspend fun updateSampleAt(index: Int) {
        val bytes = Res.readBytes(DrawableBundle.entries[index].path)
        val (viewBox, pathData) = readPathString(bytes)
        samples = makePath(viewBox, pathData, CONTENT_HALF_EXTENT).sample(_uiState.value.samplingRate)

        // Balance out the period and fading durations based on the path length
        periodDuration = samples.size.toFloat() / 1.5f
        fadingDuration = 1.0f / (0.05f * periodDuration)
    }

    fun addArrow() {
        val currentArrowCount = _uiState.value.arrowCount
        if (currentArrowCount == _arrowStates.size) {
            val newArrowIndex = _arrowStates.size
            // Get the frequency of the new arrow, the sequence is: 0, -1, 1, -2, 2, ...
            val f = if (newArrowIndex % 2 == 0) newArrowIndex / 2 else -(newArrowIndex + 1) / 2
            // Estimate the magnitude and phase of the arrow at frequency f
            val (length, theta) = integrateNumerically(f)
            // Append this state to the arrow state list
            _arrowStates.add(Offset(length, theta))
        }
        _uiState.update { it.copy(arrowCount = currentArrowCount + 1) }
    }

    fun dropArrow() {
        // We don't drop arrows in the _arrowStates list, making increasing arrows later more efficient
        val currentArrowCount = _uiState.value.arrowCount
        val updatedArrowCount = if (currentArrowCount > 0) currentArrowCount - 1 else 0
        _uiState.update { it.copy(arrowCount = updatedArrowCount) }
    }

    /**
     * Numerically computes the Fourier Series coefficients for a given frequency.
     *
     * The method integrates numerically over the sampled points of a drawable path to calculate
     * the magnitude and phase of the Fourier Series coefficient corresponding to the provided frequency.
     *
     * @param frequency The frequency for which the Fourier Series coefficient is calculated.
     *                  Positive and negative frequencies are handled, corresponding to different
     *                  directional components of the rotation in the complex plane.
     * @return A [Pair] where the first value is the magnitude of the Fourier coefficient and
     *         the second value is the phase angle (in radians).
     */
    private fun integrateNumerically(frequency: Int): Pair<Float, Float> {
        val step = if (samples.size < 2) 0.0f else 1.0f / (samples.size - 1)
        val n = abs(frequency)

        var p = 0.0f
        var q = 0.0f
        var r = 0.0f
        var s = 0.0f

        // Integration
        samples.forEachIndexed { idx, (u, v) ->
            val t = step * idx.toFloat()  // fraction between 0..1
            val cosValue = cos(n.toFloat() * 2.0f * PI.toFloat() * t)
            val sinValue = sin(n.toFloat() * 2.0f * PI.toFloat() * t)

            p += u * cosValue * step
            q += v * cosValue * step
            r += u * sinValue * step
            s += v * sinValue * step
        }

        // Coordinates of the tip of this arrow
        val (x, y) = if (frequency < 0) {
            (p - s) to (q + r)
        } else {
            (p + s) to (q - r)
        }

        return sqrt(x * x + y * y) to atan2(y, x)
    }

    override fun onCleared() {
        super.onCleared()
        document.removeEventListener("visibilitychange", visibilityChangeCallback)
    }

    companion object {
        /**
         * A constant representing the half extent of the content area.
         *
         * It defines the scaling factor for normalizing coordinates in the drawable path
         * to adjust them relative to the center or bounds of the path's rendering area.
         */
        const val CONTENT_HALF_EXTENT = 10.0f

        private const val FS_CURRENT_DRAWABLE_KEY = "fs_drawable_index"
    }
}

private suspend fun Animatable<Float, AnimationVector1D>.animatePlay(durationMillis: Float) {
    // Ongoing animatable retains its state when stopped, we will have to animate it to the full period
    // before starting the infinitely repeatable animation
    if (value > 0.0f) {
        val remainingDuration = (durationMillis * (1.0f - value)).toInt()
        animateTo(1.0f, tween(remainingDuration, easing = LinearEasing))
        // This resets the state to 0, otherwise we'd stuck at 1
        snapTo(0.0f)
    }
    animateTo(1.0f, infiniteRepeatable(tween(durationMillis.toInt(), easing = LinearEasing)))
}

private fun windowHidden(): Boolean = js("document.hidden")
