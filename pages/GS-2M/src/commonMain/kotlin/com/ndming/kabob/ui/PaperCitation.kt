package com.ndming.kabob.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ndming.kabob.gs_2m.generated.resources.Res
import com.ndming.kabob.theme.getJetBrainsMonoFamily
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun PaperCitation(modifier: Modifier = Modifier) {
    var bytes by remember { mutableStateOf(ByteArray(0)) }
    LaunchedEffect(Unit) {
        bytes = Res.readBytes("files/citation.txt")
    }

    val fontFamily = getJetBrainsMonoFamily()

    OutlinedCard(modifier = modifier.widthIn(max = 1200.dp).fillMaxWidth().padding(24.dp)) {
        Column{
            Text(
                text = "BibTeX",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 32.dp, bottom = 24.dp),
            )

            if (bytes.isEmpty()) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = bytes.decodeToString(),
                    modifier = Modifier.padding(horizontal = 48.dp),
                    fontFamily = fontFamily,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}