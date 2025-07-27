package com.ndming.kabob.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwitchLeft
import androidx.compose.material.icons.filled.SwitchRight
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun Switcher(
    sceneName: String,
    fontFamily: FontFamily,
    portrait: Boolean,
    modifier: Modifier = Modifier,
    onSwitchL: () -> Unit,
    onSwitchR: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (!portrait) {
            Row {
                Spacer(Modifier.width(72.dp))
                FilledTonalButton(
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                    onClick = onSwitchL,
                ) {
                    Icon(Icons.Default.SwitchLeft,null)
                }
            }
        } else {
            FilledTonalButton(
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                onClick = onSwitchL,
            ) {
                Icon(Icons.Default.SwitchLeft,null)
            }
        }

        AnimatedContent(
            targetState = sceneName,
            contentAlignment = Alignment.Center,
            transitionSpec = {
                val contentEnter = fadeIn(tween(800)) + slideInVertically(tween(600)) { -it / 2 }
                val contentExit = fadeOut(tween(200)) + slideOutVertically(tween(400)) { it / 2 }
                contentEnter.togetherWith(contentExit)
            },
        ) { name ->
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Medium,
            )
        }

        if (!portrait) {
            Row {
                FilledTonalButton(
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                    onClick = onSwitchR,
                ) {
                    Icon(Icons.Default.SwitchRight,null)
                }
                Spacer(Modifier.width(72.dp))
            }
        } else {
            FilledTonalButton(
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                onClick = onSwitchR,
            ) {
                Icon(Icons.Default.SwitchRight,null)
            }
        }
    }
}