package com.ndming.kabob.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    portrait: Boolean = false,
) {
    HomePage(portrait, modifier)
}