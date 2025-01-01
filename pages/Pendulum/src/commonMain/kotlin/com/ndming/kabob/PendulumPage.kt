package com.ndming.kabob

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ndming.kabob.pendulum.generated.resources.Res
import com.ndming.kabob.pendulum.generated.resources.pendulum_top_bar_title
import com.ndming.kabob.theme.Profile
import com.ndming.kabob.ui.KabobTopBar
import org.jetbrains.compose.resources.stringResource

@Composable
fun PendulumPage(
    uiState: PendulumUiState,
    modifier: Modifier = Modifier,
    onProfileChange: (Profile) -> Unit = {},
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            KabobTopBar(
                title = stringResource(Res.string.pendulum_top_bar_title),
                onProfileChange = onProfileChange,
            )
        }
    }
}