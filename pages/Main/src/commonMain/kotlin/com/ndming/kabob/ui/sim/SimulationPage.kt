package com.ndming.kabob.ui.sim

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import com.ndming.kabob.main.generated.resources.Res
import com.ndming.kabob.main.generated.resources.main_sim_topic_pendulum_title
import kotlinx.browser.window
import org.jetbrains.compose.resources.stringResource

private const val SIM_TOPIC_PREFIX = "simulations"

private const val SIM_TOPIC_PENDULUM = "pendulum"

@Composable
fun SimulationPage(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            SimTopic(stringResource(Res.string.main_sim_topic_pendulum_title)) {
                window.open("$SIM_TOPIC_PREFIX/$SIM_TOPIC_PENDULUM", "_self")
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SimTopic(
    topicName: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    var expandSimulateButton by remember { mutableStateOf(false) }

    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SelectionContainer {
                Text(
                    modifier = Modifier.padding(24.dp),
                    text = topicName,
                    style = MaterialTheme.typography.headlineSmall,
                )
            }

            Button(
                modifier = Modifier
                    .padding(end = 24.dp)
                    .pointerHoverIcon(PointerIcon.Hand)
                    .onPointerEvent(PointerEventType.Enter) { expandSimulateButton = true }
                    .onPointerEvent(PointerEventType.Exit) { expandSimulateButton = false },
                onClick = onClick,
            ) {
                AnimatedVisibility(visible = expandSimulateButton) {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = "Simulate",
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                )
            }
        }
    }
}