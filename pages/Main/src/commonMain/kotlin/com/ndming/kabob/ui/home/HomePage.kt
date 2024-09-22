package com.ndming.kabob.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.ndming.kabob.main.generated.resources.*
import com.ndming.kabob.theme.LocalKabobTheme
import com.ndming.kabob.theme.Profile
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

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
fun HomeAvatar(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier, shape = CircleShape,
        border = BorderStroke(width = 4.dp, color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f))
    ) {
        AnimatedContent(
            targetState = LocalKabobTheme.current.profile,
            transitionSpec = {
                val contentEnter = fadeIn(tween(800))
                val contentExit = fadeOut(tween(600))
                contentEnter.togetherWith(contentExit)
            }
        ) { profile ->
            if (profile == Profile.LIGHT) {
                Image(
                    modifier = Modifier.size(256.dp),
                    painter = painterResource(Res.drawable.home_avatar_light),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                )
            } else {
                Image(
                    modifier = Modifier.size(256.dp),
                    painter = painterResource(Res.drawable.home_avatar_dark),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                )
            }
        }
    }
}

@Composable
fun HomeHeadlines(modifier: Modifier = Modifier) {
    val headlineDetailStyle = MaterialTheme.typography.bodyLarge

    Column(modifier = modifier.fillMaxWidth()) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            if (maxWidth > 512.dp) {
                Text(
                    text = stringResource(Res.string.banner_title),
                    style = MaterialTheme.typography.displaySmall,
                )
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
            Text(
                text = stringResource(Res.string.banner_institute),
                style = headlineDetailStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.LocationOn, null,
                Modifier.padding(end = 8.dp),
                tint = LocalContentColor.current.copy(alpha = 0.6f)
            )
            Text(
                text = stringResource(Res.string.banner_location),
                style = headlineDetailStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Book, null,
                Modifier.padding(end = 8.dp),
                tint = LocalContentColor.current.copy(alpha = 0.6f)
            )
            Text(
                text = stringResource(Res.string.banner_interests),
                style = headlineDetailStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}