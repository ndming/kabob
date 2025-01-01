package com.ndming.kabob.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Loyalty
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.ndming.kabob.main.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun HomePage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Card(modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(36.dp),
                ) {
                    if (this@BoxWithConstraints.maxWidth > 920.dp) {
                        HomeAvatar()
                        Spacer(Modifier.width(32.dp))
                    }
                    HomeHeadlines()
                }
            }
        }
    }
}

@Composable
private fun HomeAvatar(modifier: Modifier = Modifier) {
    Image(
        imageVector = vectorResource(Res.drawable.kabob_logo),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier.size(256.dp),
    )
}

@Composable
private fun HomeHeadlines(modifier: Modifier = Modifier) {
    val headlineDetailStyle = MaterialTheme.typography.bodyLarge

    Column(modifier = modifier.fillMaxWidth()) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            if (maxWidth > 512.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SelectionContainer {
                        Text(
                            text = stringResource(Res.string.banner_title),
                            style = MaterialTheme.typography.displaySmall,
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Image(
                        imageVector = vectorResource(Res.drawable.vietnam_flag),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = modifier.height(20.dp),
                    )
                }
            } else {
                HomeAvatar(Modifier.align(Alignment.Center))
            }
        }

        Spacer(Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.School, null,
                Modifier.padding(end = 8.dp),
                tint = LocalContentColor.current.copy(alpha = 0.6f)
            )
            SelectionContainer {
                Text(
                    text = stringResource(Res.string.banner_profession),
                    style = headlineDetailStyle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.LocationOn, null,
                Modifier.padding(end = 8.dp),
                tint = LocalContentColor.current.copy(alpha = 0.6f)
            )
            SelectionContainer {
                Text(
                    text = stringResource(Res.string.banner_location),
                    style = headlineDetailStyle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Loyalty, null,
                Modifier.padding(end = 8.dp),
                tint = LocalContentColor.current.copy(alpha = 0.6f)
            )
            SelectionContainer {
                Text(
                    text = stringResource(Res.string.banner_interests),
                    style = headlineDetailStyle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}