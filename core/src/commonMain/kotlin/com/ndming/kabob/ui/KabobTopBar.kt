package com.ndming.kabob.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import com.ndming.kabob.core.generated.resources.Res
import com.ndming.kabob.core.generated.resources.github
import com.ndming.kabob.core.generated.resources.linkedin
import com.ndming.kabob.theme.Profile
import kotlinx.browser.window
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KabobTopBar(
    title: @Composable () -> Unit,
    currentProfile: Profile,
    onProfileChange: (Profile) -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
) {
    TopAppBar(
        modifier = modifier.fillMaxWidth(),
        title = title,
        navigationIcon = navigationIcon,
        actions = {
            // Light/dark mode button
            IconButton(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .pointerHoverIcon(PointerIcon.Hand),
                onClick = {
                    onProfileChange(if (currentProfile == Profile.LIGHT) Profile.DARK else Profile.LIGHT)
                }
            ) {
                AnimatedContent(
                    targetState = currentProfile,
                    contentAlignment = Alignment.Center,
                    transitionSpec = {
                        val contentEnter = fadeIn(tween(800)) + slideInVertically(tween(600)) { -it / 2 }
                        val contentExit = fadeOut(tween(200)) + slideOutVertically(tween(400)) { it / 2 }
                        contentEnter.togetherWith(contentExit)
                    }
                ) { currentProfile ->
                    val icon = if (currentProfile == Profile.LIGHT) Icons.Default.LightMode else Icons.Default.DarkMode
                    Icon(imageVector = icon, contentDescription = null)
                }
            }

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
    )
}
