package com.ndming.kabob.markup

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HyperlinkText(
    linkText: String,
    linkUrl: String,
    modifier: Modifier = Modifier,
    prefix: @Composable () -> Unit = {},
    suffix: @Composable () -> Unit = {},
) {
    FlowRow(modifier = modifier) {
        prefix()

        Column(modifier = Modifier.width(IntrinsicSize.Max)) {
            Row {
                Text(
                    maxLines = 1,
                    text = buildAnnotatedString {
                        withLink(
                            LinkAnnotation.Url(
                                linkUrl,
                                TextLinkStyles(
                                    SpanStyle(
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.Light,
                                    )
                                )
                            )
                        ) {
                            append(linkText)
                        }
                    }
                )
                Column {
                    Spacer(Modifier.height(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowOutward,
                        contentDescription = linkUrl,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
        }

        suffix()
    }
}

@Composable
fun HyperlinkText(
    linkText: String,
    linkUrl: String,
    modifier: Modifier = Modifier,
    prefix: String = "",
    suffix: String = "",
) {
    HyperlinkText(
        linkText = linkText,
        linkUrl = linkUrl,
        modifier = modifier,
        prefix = { Text(prefix) },
        suffix = { Text(suffix) },
    )
}