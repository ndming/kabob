package com.ndming.kabob.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ndming.kabob.gs_2m.generated.resources.Res
import com.ndming.kabob.gs_2m.generated.resources.gs2m_abstract
import org.jetbrains.compose.resources.stringResource

@Composable
fun PaperAbstract(modifier: Modifier = Modifier) {
    Card(modifier = modifier.widthIn(max = 1132.dp).fillMaxWidth().padding(24.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Abstract",
                modifier = Modifier.fillMaxWidth().padding(top = 32.dp, bottom = 24.dp),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
            )

            Text(
                text = stringResource(Res.string.gs2m_abstract),
                textAlign = TextAlign.Justify,
                modifier = Modifier.padding(horizontal = 48.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}