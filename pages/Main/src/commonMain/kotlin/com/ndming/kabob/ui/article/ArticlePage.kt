package com.ndming.kabob.ui.article

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
import com.ndming.kabob.main.generated.resources.Res
import com.ndming.kabob.main.generated.resources.main_article_topic_brdf_title
import com.ndming.kabob.main.generated.resources.main_article_topic_svm_title
import com.ndming.kabob.theme.getJetBrainsMonoFamily
import kotlinx.browser.window
import org.jetbrains.compose.resources.stringResource

private const val ARTICLE_TOPIC_PREFIX = "articles"

private const val ARTICLE_TOPIC_SVM  = "support-vector-machine"
private const val ARTICLE_TOPIC_BRDF = "bidirectional-reflectance-distribution-function"

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ArticlePage(modifier: Modifier = Modifier) {
    FlowRow(modifier = modifier.padding(start = 24.dp)) {
        ArticleTopic(
            modifier = Modifier.padding(end = 24.dp, bottom = 24.dp),
            name = stringResource(Res.string.main_article_topic_svm_title),
            tags = listOf(ArticleTag.MachineLearning),
            onClick = { window.open("$ARTICLE_TOPIC_PREFIX/$ARTICLE_TOPIC_SVM", "_self") },
        )

        ArticleTopic(
            modifier = Modifier.padding(end = 24.dp, bottom = 24.dp),
            name = stringResource(Res.string.main_article_topic_brdf_title),
            tags = listOf(ArticleTag.ComputerGraphics),
            onClick = { window.open("$ARTICLE_TOPIC_PREFIX/$ARTICLE_TOPIC_BRDF", "_self") },
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
            .width(160.dp)
            .wrapContentSize()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.8f)
                .pointerHoverIcon(PointerIcon.Hand),
            onClick = onClick,
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
                            style = MaterialTheme.typography.labelLarge,
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
        )
    }
}
