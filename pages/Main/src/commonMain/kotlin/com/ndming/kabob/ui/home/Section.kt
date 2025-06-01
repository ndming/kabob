package com.ndming.kabob.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Hexagon
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun Section(
    title: String,
    iconImage: ImageVector,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            FilledIconToggleButton(checked = false, onCheckedChange = {}) {
                Icon(
                    imageVector = iconImage,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.tertiary,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))

            Text(text = title, style = MaterialTheme.typography.titleLarge)
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))

        content()
    }
}

@Composable
fun SectionItem(
    leadingText: String,
    modifier: Modifier = Modifier,
    portrait: Boolean = false,
    trailingText: String? = null,
) {
    Row(modifier = modifier) {
        Column {
            Spacer(Modifier.height(8.dp))
            Icon(
                imageVector = Icons.Filled.Hexagon,
                contentDescription = leadingText,
                tint = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(8.dp),
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                    append(leadingText)

                    if (trailingText != null && !portrait) {
                        append(": ")
                    }
                }
                if (trailingText != null && !portrait) {
                    append(trailingText)
                }
            }
        )
    }
}

@Composable
fun SectionItem(
    leadingText: String,
    leadingLink: String,
    modifier: Modifier = Modifier,
    portrait: Boolean = false,
    trailingText: String? = null,
) {
    Row(modifier = modifier) {
        Column {
            Spacer(Modifier.height(8.dp))
            Icon(
                imageVector = Icons.Filled.Hexagon,
                contentDescription = leadingText,
                tint = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(8.dp),
            )
        }
        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.width(IntrinsicSize.Min)) {
            Row {
                Text(
                    text = buildAnnotatedString {
                        withLink(
                            LinkAnnotation.Url(
                                leadingLink,
                                TextLinkStyles(
                                    SpanStyle(
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.Light,
                                    )
                                )
                            )
                        ) {
                            append(leadingText)
                        }
                    }
                )
                Column {
                    Spacer(Modifier.height(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowOutward,
                        contentDescription = leadingLink,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
        }
        if (trailingText != null && !portrait) {
            Text(text = ": $trailingText")
        }
    }
}