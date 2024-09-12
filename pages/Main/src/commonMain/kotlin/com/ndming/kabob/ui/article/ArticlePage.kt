package com.ndming.kabob.ui.article

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ndming.kabob.theme.getJetBrainsMonoFamily
import kotlinx.browser.window

private const val ARTICLE_TOPIC_PREFIX = "articles"

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ArticlePage(modifier: Modifier = Modifier) {
    FlowRow(modifier = modifier) {
        ArticleTopic(
            name = "Support Vector Machine",
            tags = listOf(ArticleTag.MachineLearning),
            onClick = { window.open("$ARTICLE_TOPIC_PREFIX/support-vector-machine", "_self") },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ArticleTopic(
    name: String,
    tags: List<ArticleTag>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val tagLabelFamily = getJetBrainsMonoFamily()

    Column(
        modifier = modifier
            .width(180.dp)
            .wrapContentSize()
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable { onClick() }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.8f)
        ) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            ) {
                tags.forEach { tag ->
                    Surface(
                        modifier = Modifier.wrapContentSize(),
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                    ) {
                        Text(
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp),
                            text = tag.tagLabel,
                            fontFamily = tagLabelFamily,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }
            }
        }

        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}
