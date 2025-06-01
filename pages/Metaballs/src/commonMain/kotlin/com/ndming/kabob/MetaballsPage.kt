package com.ndming.kabob

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ndming.kabob.metaballs.generated.resources.Res
import com.ndming.kabob.metaballs.generated.resources.metaballs_top_bar_title
import com.ndming.kabob.theme.Profile
import com.ndming.kabob.ui.KabobTopBar
import org.jetbrains.compose.resources.stringResource

@Composable
fun MetaballsPage(
    uiState: MetaballsUiState,
    modifier: Modifier = Modifier,
    onProfileChange: (Profile) -> Unit = {},
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            KabobTopBar(
                title = stringResource(Res.string.metaballs_top_bar_title),
                onProfileChange = onProfileChange,
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Engineering,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = LocalContentColor.current.copy(alpha = 0.6f)
                )
            }
        }
    }
}