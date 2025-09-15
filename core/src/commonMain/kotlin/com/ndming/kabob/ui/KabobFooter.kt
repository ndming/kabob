package com.ndming.kabob.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ndming.kabob.core.generated.resources.Res
import com.ndming.kabob.core.generated.resources.kabob_logo
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun KabobFooter(
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 24.dp,
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

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = horizontalPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = bytes.decodeToString(),
                    modifier = Modifier.padding(vertical = 12.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                )
                Image(
                    imageVector = vectorResource(Res.drawable.kabob_logo),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                )
            }
        }
    }
}