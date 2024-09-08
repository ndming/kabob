package com.ndming.kabob.ui.visual

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.browser.window

private const val VISUAL_TOPIC_PREFIX = "visualizations"

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VisualPage(modifier: Modifier = Modifier) {
    FlowRow(modifier = modifier) {
        VisualTopic(
            topicName = "Fourier Series",
            onClick = { window.open("${VISUAL_TOPIC_PREFIX}/fs", "_self") }
        )
    }
}

@Composable
private fun VisualTopic(
    topicName: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    cover: @Composable () -> Unit = {},
) {
    ElevatedCard(
        modifier = modifier
            .width(360.dp)
            .aspectRatio(1.5f)
            .pointerHoverIcon(PointerIcon.Hand),
        onClick = onClick,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.7f),
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                cover()
            }
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    modifier = Modifier.padding(start = 24.dp),
                    text = topicName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}