package com.ndming.kabob

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ndming.kabob.theme.Profile
import com.ndming.kabob.ui.KabobTopBar
import com.ndming.kabob.ui.PaperBanner
import com.ndming.kabob.ui.PaperCover

@Composable
fun Gs2mPage(
    uiState: Gs2mUiState,
    modifier: Modifier = Modifier,
    onProfileChange: (Profile) -> Unit = {},
) {
    val scrollState = rememberScrollState()

    Surface(modifier = modifier.fillMaxSize()) {
        SelectionContainer {
            Column(modifier = Modifier.fillMaxSize()) {
                KabobTopBar(
                    title = if (scrollState.value > 0) "GS-2M" else "",
                    onProfileChange = onProfileChange,
                )

                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val portrait = maxWidth / maxHeight < 1.0f

                    Column(
                        modifier = Modifier.fillMaxWidth().verticalScroll(scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        PaperBanner()

                        PaperCover(portrait)

                        Spacer(modifier = Modifier.height(56.dp))

                        Card(modifier = Modifier.widthIn(max = 1024.dp).fillMaxWidth().padding(24.dp)) {
                            Column {
                                Text(
                                    text = "Abstract",
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                                    style = MaterialTheme.typography.headlineSmall,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Medium,
                                )

                            }
                        }
                    }
                }
            }
        }
    }
}