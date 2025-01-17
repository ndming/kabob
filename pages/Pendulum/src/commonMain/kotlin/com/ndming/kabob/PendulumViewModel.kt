package com.ndming.kabob

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.util.fastCoerceAtLeast
import com.ndming.kabob.theme.ThemeAwareViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.*

data class PendulumUiState(
    val theta: Float = 0.0f,
    val thetaDot: Float = 0.0f,
    val friction: Float = 0.15f,
    val armLength: Float = 2.5f,
    val xCenter: Float = 0.0f,
    val yScale: Float = 1.0f,
)

class PendulumViewModel : ThemeAwareViewModel() {
    private val _uiState = MutableStateFlow(PendulumUiState())
    val uiState: StateFlow<PendulumUiState> = _uiState.asStateFlow()

    private var swingJob: Job? = null
    private val velocityTracker = VelocityTracker()

    fun dragPendulum(amount: Offset, uptimeMillis: Long, armLengthPx: Float, scope: CoroutineScope) {
        swingJob?.cancel()
        swingJob = null

        var theta = _uiState.value.theta
        theta += cos(theta) * (amount.x / armLengthPx) + sin(theta) * (-amount.y / armLengthPx)

        _uiState.update { it.copy(theta = theta, thetaDot = 0.0f) }
        velocityTracker.addPosition(uptimeMillis, Offset(theta, 0.0f))

        updateCenter(scope)
        resetScale(scope)
    }

    fun animateSwing(scope: CoroutineScope) {
        swingJob?.cancel()
        swingJob = scope.launch {
            var thetaDot = velocityTracker.calculateVelocity().x

            _uiState.update { it.copy(thetaDot = thetaDot) }
            velocityTracker.resetTracking()

            var lastTime = withFrameNanos { it }

            while (isActive) {
                var theta = _uiState.value.theta
                val thetaDoubleDot = getThetaDoubleDot(theta, thetaDot)

                val now = withFrameNanos { it }
                val deltaTime = ((now - lastTime).toFloat() / 1_000_000_000f).takeIf { it <= DELTA_T  } ?: DELTA_T
                lastTime = now

                theta += thetaDot * deltaTime
                thetaDot += thetaDoubleDot * deltaTime

                _uiState.update { it.copy(theta = theta, thetaDot = thetaDot) }
                updateCenter(scope)
                updateScale(PHASE_SPACE_VIEWPORT_HALF_EXTENT, scope)
            }
        }
    }

    private fun getThetaDoubleDot(theta: Float, thetaDot: Float): Float {
        val l  = _uiState.value.armLength
        val mu = _uiState.value.friction
        return -mu * thetaDot - (GRAVITY / l) * sin(theta)
    }

    fun changeArmLength(length: Float) {
        _uiState.update { it.copy(armLength = length) }
    }

    fun changeFriction(friction: Float) {
        _uiState.update { it.copy(friction = friction) }
    }

    private val centerAnimator = Animatable(0.0f)

    private fun updateCenter(scope: CoroutineScope) {
        val theta = _uiState.value.theta
        val sign = if (theta >= 0.0f) 1.0f else -1.0f
        val multiplier = sign * floor(abs(theta) / PI.toFloat()).let { if (it % 2 == 0.0f) it else it + 1 }
        val center = multiplier * PI.toFloat()

        if (centerAnimator.targetValue != center) {
            scope.launch {
                centerAnimator.animateTo(targetValue = center, animationSpec = tween(650)) {
                    _uiState.update { it.copy(xCenter = this.value) }
                }
            }
        }
    }

    private val scaleAnimator = Animatable(1.0f)
    private var scaleMultiplier = 1.0f

    private fun updateScale(halfExtent: Float, scope: CoroutineScope) {
        if (abs(_uiState.value.thetaDot) > halfExtent * scaleMultiplier) {
            scaleMultiplier = if (scaleMultiplier == 1.0f && abs(_uiState.value.thetaDot) <= halfExtent + 1.0f) 1.0f
            else if (abs(_uiState.value.thetaDot) > halfExtent + 1.0f) halfExtent
            else scaleMultiplier + halfExtent

            scope.launch {
                scaleAnimator.animateTo(scaleMultiplier, animationSpec = tween(800)) {
                    _uiState.update { it.copy(yScale = this.value) }
                }
            }
        } else if (
            scaleMultiplier >= halfExtent &&
            abs(_uiState.value.thetaDot) < halfExtent * (scaleMultiplier - halfExtent).fastCoerceAtLeast(1.0f)
            ) {
            scaleMultiplier = (scaleMultiplier - halfExtent).fastCoerceAtLeast(1.0f)
            scope.launch {
                scaleAnimator.animateTo(scaleMultiplier, animationSpec = tween(1200)) {
                    _uiState.update { it.copy(yScale = this.value) }
                }
            }
        }
    }

    private fun resetScale(scope: CoroutineScope) {
        scope.launch {
            scaleAnimator.animateTo(1.0f, animationSpec = tween(600)) {
                _uiState.update { it.copy(yScale = this.value) }
            }
        }
        scaleMultiplier = 1.0f
    }

    companion object {
        private const val GRAVITY = 9.810f  // m/s^2
        private const val DELTA_T = 0.016f  // ~60FPS

        const val PHASE_SPACE_VIEWPORT_HALF_EXTENT = 4.0f
    }
}
