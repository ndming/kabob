package com.ndming.kabob.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ndming.kabob.core.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun KabobFooter(
    modifier: Modifier = Modifier,
    startPadding: Dp = 24.dp,
    surfaceColor: Color = MaterialTheme.colorScheme.surface
) {
    var bytes by remember { mutableStateOf(ByteArray(0)) }
    LaunchedEffect(Unit) {
        bytes = Res.readBytes("files/footer.txt")
    }

    Surface(
        color = surfaceColor,
        modifier = modifier
    ) {
        Column {
            HorizontalDivider()
            Text(
                text = bytes.decodeToString(),
                modifier = Modifier.padding(start = startPadding).padding(vertical = 12.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
            )
        }
    }
}