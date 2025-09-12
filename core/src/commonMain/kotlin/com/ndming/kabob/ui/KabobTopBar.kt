package com.ndming.kabob.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import com.ndming.kabob.core.generated.resources.Res
import com.ndming.kabob.core.generated.resources.github
import com.ndming.kabob.core.generated.resources.linkedin
import com.ndming.kabob.theme.LocalKabobTheme
import com.ndming.kabob.theme.Profile
import kotlinx.browser.window
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KabobTopBar(
    header: @Composable () -> Unit,
    currentProfile: Profile,
    onProfileChange: (Profile) -> Unit,
    modifier: Modifier = Modifier,
    concise: Boolean = false,
    headerIcon: @Composable () -> Unit = {},
) {
    Surface(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                headerIcon()
                header()
            }

            // Actions
            Row {
                // Light/dark mode button
                IconButton(
                    modifier = Modifier
                        .padding(start = 12.dp, end = if (concise) 12.dp else 0.dp)
                        .pointerHoverIcon(PointerIcon.Hand),
                    onClick = {
                        onProfileChange(if (currentProfile == Profile.LIGHT) Profile.DARK else Profile.LIGHT)
                    }
                ) {
                    val damping = Spring.DampingRatioMediumBouncy
                    AnimatedContent(
                targetState = currentProfile,
                        contentAlignment = Alignment.Center,
                        transitionSpec = {
                            val contentEnter = fadeIn(spring(damping)) + slideInVertically(spring(damping)) { -it / 2 }
                            val contentExit = fadeOut(spring(damping)) + slideOutVertically(spring(damping)) { it / 2 }
                            contentEnter.togetherWith(contentExit)
                        }
                    ) { currentProfile ->
                        val icon = if (currentProfile == Profile.LIGHT) Icons.Default.DarkMode else Icons.Default.LightMode
                        Icon(imageVector = icon, contentDescription = null)
                    }
                }

                if (!concise) {
                    // GitHub button
                    IconButton(
                        modifier = Modifier
                            .pointerHoverIcon(PointerIcon.Hand)
                            .padding(start = 12.dp),
                        onClick = { window.open("https://github.com/ndming", "_blank") },
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.github),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // LinkedIn button
                    IconButton(
                        modifier = Modifier
                            .pointerHoverIcon(PointerIcon.Hand)
                            .padding(start = 12.dp),
                        onClick = { window.open("https://www.linkedin.com/in/minh-nguyen-59671b194/", "_blank") },
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.linkedin),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Email button
                    IconButton(
                        modifier = Modifier
                            .pointerHoverIcon(PointerIcon.Hand)
                            .padding(horizontal = 12.dp),
                        onClick = { window.open("mailto:ndminh1101@gmail.com", "_blank") },
                    ) {
                        Icon(Icons.Default.Mail, null)
                    }
                }
            }
        }
    }
}

@Composable
fun KabobTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onProfileChange: (Profile) -> Unit,
) {
    KabobTopBar(
        modifier = modifier,
        concise = true,
        header = {
            SelectionContainer {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                )
            }
        },
        currentProfile = LocalKabobTheme.current.profile,
        onProfileChange = onProfileChange,
        headerIcon = {
            IconButton(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .pointerHoverIcon(PointerIcon.Hand),
                onClick = { window.open("https://ndming.github.io/", "_self") },
            ) {
                Icon(Icons.Default.Home, contentDescription = null)
            }
        }
    )
}
