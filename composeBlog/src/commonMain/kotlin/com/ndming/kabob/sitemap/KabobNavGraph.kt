package com.ndming.kabob.sitemap

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ndming.kabob.composeblog.generated.resources.Res
import com.ndming.kabob.composeblog.generated.resources.nav_panel_home_label
import com.ndming.kabob.composeblog.generated.resources.nav_panel_visual_label
import org.jetbrains.compose.resources.StringResource

@Composable
fun KabobNavGraph(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
) {
    NavHost(
        modifier = modifier.fillMaxSize(),
        navController = navController,
        startDestination = startDestination,
        contentAlignment = Alignment.Center,
    ) {
        composable(route = KabobTopic.HOME.route) {
            HomeRoute()
        }

        composable(route = KabobTopic.VISUAL.route) {
            VisualRoute()
        }
    }
}

enum class KabobTopic(val route: String, val label: StringResource, val icon: ImageVector) {
    HOME("home", Res.string.nav_panel_home_label, Icons.Default.Home),
    VISUAL("visual", Res.string.nav_panel_visual_label, Icons.Default.Gesture),
}