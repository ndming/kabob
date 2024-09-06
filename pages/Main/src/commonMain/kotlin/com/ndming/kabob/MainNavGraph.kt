package com.ndming.kabob

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.ndming.kabob.main.generated.resources.*
import com.ndming.kabob.ui.home.HomeRoute
import com.ndming.kabob.ui.visual.VisualRoute
import org.jetbrains.compose.resources.StringResource

@Composable
fun MainNavGraph(
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
        composable(
            route = MainRoute.Home.route,
            enterTransition = { fadeIn(tween(400)) },
            exitTransition = { fadeOut(tween(300)) },
        ) {
            HomeRoute()
        }

        composable(
            route = MainRoute.Visual.route,
            enterTransition = { fadeIn(tween(400)) },
            exitTransition = { fadeOut(tween(300)) },
        ) {
            VisualRoute()
        }
    }
}

enum class MainRoute(
    val route: String,
    val label: StringResource,
    val title: StringResource,
    val icon: ImageVector
) {
    Home(
        route = "home",
        label = Res.string.nav_panel_home_label,
        title = Res.string.top_bar_home_title,
        icon  = Icons.Default.Home,
    ),
    Visual(
        route = "visual",
        label = Res.string.nav_panel_visual_label,
        title = Res.string.top_bar_visual_title,
        icon  = Icons.Default.Gesture,
    ),
}