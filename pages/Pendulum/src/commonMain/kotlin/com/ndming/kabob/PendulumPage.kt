package com.ndming.kabob

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ndming.kabob.pendulum.generated.resources.Res
import com.ndming.kabob.pendulum.generated.resources.pendulum_top_bar_title
import com.ndming.kabob.theme.Profile
import com.ndming.kabob.theme.getNotoSansMathFamily
import com.ndming.kabob.ui.KabobTopBar
import com.ndming.kabob.ui.Pendulum
import com.ndming.kabob.ui.PhaseSpace
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.compose.resources.stringResource

private const val PHASE_SPACE_VIEW_ASPECT_RATIO = 1.3f

@Composable
fun PendulumPage(
    uiState: PendulumUiState,
    modifier: Modifier = Modifier,
    onProfileChange: (Profile) -> Unit = {},
    onPendulumDrag: (Offset, Long, Float, CoroutineScope) -> Unit = { _, _, _, _ -> },
    onAnimateSwing: (CoroutineScope) -> Unit = {},
    onArmLengthChange: (Float) -> Unit = {},
    onFrictionChange: (Float) -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()

    var openSettingDialog by remember { mutableStateOf(false) }

    Surface(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            KabobTopBar(
                title = stringResource(Res.string.pendulum_top_bar_title),
                onProfileChange = onProfileChange,
            )

            // Main content
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val ratio = maxWidth / maxHeight

                // Close setting dialog when switching to certain configurations
                LaunchedEffect(ratio) {
                    if (ratio < 2.2f || ratio >= 3.8f) {
                        openSettingDialog = false
                    }
                }

                if (ratio > 1.0f) {
                    Row(
                        modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(2.0f, false)
                                .aspectRatio(PHASE_SPACE_VIEW_ASPECT_RATIO, matchHeightConstraintsFirst = ratio > 1.8f)
                                .fillMaxSize()
                                .padding(24.dp),
                            shape  = MaterialTheme.shapes.large,
                            border = BorderStroke(width = 3.dp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)),
                        ) {
                            PhaseSpace(
                                armLength = uiState.armLength,
                                friction  = uiState.friction,
                                theta     = uiState.theta,
                                thetaDot  = uiState.thetaDot,
                                xCenter   = uiState.xCenter,
                                yScale    = uiState.yScale,
                                yLimit    = PendulumViewModel.PHASE_SPACE_VIEWPORT_HALF_EXTENT,
                                viewportAspectRatio = PHASE_SPACE_VIEW_ASPECT_RATIO,
                            )
                        }

                        if (ratio >= 3.8f) {
                            PendulumSettings(
                                modifier = Modifier
                                    .weight(1.0f, false)
                                    .padding(start = 16.dp, end = 32.dp),
                                armLength = uiState.armLength,
                                friction  = uiState.friction,
                                withODE   = true,
                                onArmLengthChange = onArmLengthChange,
                                onFrictionChange  = onFrictionChange,
                            )
                        }

                        Column(modifier = Modifier.weight(1.0f, false)) {
                            Pendulum(
                                modifier = Modifier
                                    .weight(1.0f, false)
                                    .padding(end = 32.dp, bottom = 16.dp)
                                    .padding(32.dp),
                                theta  = uiState.theta,
                                fillHeightFirst = ratio > 3.0f,
                                showSettingButton = ratio >= 2.2f && ratio < 3.8f,
                                onSettingButtonClick = { openSettingDialog = true },
                                onRelease = { onAnimateSwing(coroutineScope) },
                            ) { amount, uptime, armLengthPx ->
                                onPendulumDrag(amount, uptime, armLengthPx, coroutineScope)
                            }

                            if (ratio < 2.2f) {
                                PendulumSettings(
                                    modifier = Modifier
                                        .weight(0.5f, false)
                                        .padding(horizontal = 24.dp)
                                        .padding(bottom = 16.dp, top = 32.dp),
                                    armLength = uiState.armLength,
                                    friction  = uiState.friction,
                                    onArmLengthChange = onArmLengthChange,
                                    onFrictionChange  = onFrictionChange,
                                )
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Surface {
                            PhaseSpace(
                                modifier  = Modifier.aspectRatio(PHASE_SPACE_VIEW_ASPECT_RATIO, false),
                                armLength = uiState.armLength,
                                friction  = uiState.friction,
                                theta     = uiState.theta,
                                thetaDot  = uiState.thetaDot,
                                xCenter   = uiState.xCenter,
                                yScale    = uiState.yScale,
                                yLimit    = PendulumViewModel.PHASE_SPACE_VIEWPORT_HALF_EXTENT,
                                viewportAspectRatio = PHASE_SPACE_VIEW_ASPECT_RATIO,
                            )
                        }

                        Pendulum(
                            modifier = Modifier.padding(64.dp),
                            theta  = uiState.theta,
                            fillHeightFirst = ratio > 0.55f,
                            showSettingButton = true,
                            onSettingButtonClick = { openSettingDialog = true },
                            onRelease = { onAnimateSwing(coroutineScope) },
                        ) { amount, uptime, armLengthPx ->
                            onPendulumDrag(amount, uptime, armLengthPx, coroutineScope)
                        }
                    }
                }
            }

            if (openSettingDialog) {
                Dialog(onDismissRequest = { openSettingDialog = false }) {
                    Card {
                        Column(modifier = Modifier.padding(horizontal = 32.dp)) {
                            PendulumSettings(
                                modifier = Modifier.padding(vertical = 32.dp),
                                armLength = uiState.armLength,
                                friction  = uiState.friction,
                                withODE   = true,
                                onArmLengthChange = onArmLengthChange,
                                onFrictionChange  = onFrictionChange,
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 32.dp, bottom = 16.dp),
                                horizontalArrangement = Arrangement.End,
                            ) {
                                TextButton(
                                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                                    onClick = { openSettingDialog = false }
                                ) {
                                    Text("Confirm")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PendulumSettings(
    armLength: Float,
    friction: Float,
    modifier: Modifier = Modifier,
    withODE: Boolean = false,
    onArmLengthChange: (Float) -> Unit = {},
    onFrictionChange: (Float) -> Unit = {},
) {
    val notoSansMathFamily = getNotoSansMathFamily()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (withODE) {
            Text(
                modifier = modifier,
                text = "\uD835\uDF03''(\uD835\uDC61) = \u2212\uD835\uDF07\uD835\uDF03'(\uD835\uDC61) " +
                        "\u2212(\uD835\uDC54/\uD835\uDC3F) \uD835\uDC2C\uD835\uDC22\uD835\uDC27(\uD835\uDF03(\uD835\uDC61))",
                fontFamily = notoSansMathFamily,
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(Modifier.height(64.dp))
        }

        Text(
            modifier = Modifier
                .align(Alignment.Start)
                .padding(horizontal = 16.dp),
            text = buildAnnotatedString {
                append("Arm Length (")
                withStyle(SpanStyle(fontFamily = notoSansMathFamily)) {
                    if (withODE) {
                        append("\uD835\uDC3F")
                    } else {
                        append("\uD835\uDC5A")
                    }
                }
                append(")")
            }
        )
        Slider(
            modifier = Modifier
                .pointerHoverIcon(PointerIcon.Hand)
                .padding(horizontal = 16.dp),
            value = armLength,
            onValueChange = onArmLengthChange,
            valueRange = 0.5f..6.0f,
        )

        Spacer(Modifier.height(16.dp))

        Text(
            modifier = Modifier
                .align(Alignment.Start)
                .padding(horizontal = 16.dp),
            text = buildAnnotatedString {
                append("Friction")
                if (withODE) {
                    append(" (")
                    withStyle(SpanStyle(fontFamily = notoSansMathFamily)) {
                        append("\uD835\uDF07")
                    }
                    append(")")
                }
            }
        )
        Slider(
            modifier = Modifier
                .pointerHoverIcon(PointerIcon.Hand)
                .padding(horizontal = 16.dp),
            value = friction,
            onValueChange = onFrictionChange,
            valueRange = 0.0f..2.0f,
        )
    }
}
