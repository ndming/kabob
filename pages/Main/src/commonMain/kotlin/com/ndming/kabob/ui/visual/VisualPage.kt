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
import com.ndming.kabob.main.generated.resources.Res
import com.ndming.kabob.main.generated.resources.main_visual_topic_fs_title
import com.ndming.kabob.main.generated.resources.main_visual_topic_metaball_title
import kotlinx.browser.window
import org.jetbrains.compose.resources.stringResource

private const val VISUAL_TOPIC_PREFIX = "visualizations"

private const val VISUAL_TOPIC_FS       = "fourier-series"
private const val VISUAL_TOPIC_METABALL = "metaballs"

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VisualPage(modifier: Modifier = Modifier) {
    FlowRow(modifier = modifier.padding(start = 24.dp)) {
        VisualTopic(
            modifier = Modifier.padding(end = 24.dp, bottom = 24.dp),
            topicName = stringResource(Res.string.main_visual_topic_fs_title),
            onClick = { window.open("${VISUAL_TOPIC_PREFIX}/$VISUAL_TOPIC_FS", "_self") }
        )

        VisualTopic(
            modifier = Modifier.padding(end = 24.dp, bottom = 24.dp),
            topicName = stringResource(Res.string.main_visual_topic_metaball_title),
            onClick = { window.open("${VISUAL_TOPIC_PREFIX}/$VISUAL_TOPIC_METABALL", "_self") }
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