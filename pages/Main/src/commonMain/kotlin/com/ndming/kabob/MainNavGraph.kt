package com.ndming.kabob

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Science
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ndming.kabob.main.generated.resources.*
import com.ndming.kabob.ui.home.HomePage
import com.ndming.kabob.ui.pub.PublicationPage
import com.ndming.kabob.ui.sim.SimulationRoute
import com.ndming.kabob.ui.visual.VisualRoute
import org.jetbrains.compose.resources.StringResource

@Composable
fun MainNavGraph(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    portrait: Boolean = false,
    scrollState: ScrollState = rememberScrollState()
) {
    NavHost(
        modifier = modifier.fillMaxSize(),
        navController = navController,
        startDestination = startDestination,
        contentAlignment = Alignment.Center,
    ) {
        composable(
            route = MainTopic.Home.route,
            enterTransition = { fadeIn(tween(400)) },
            exitTransition = { fadeOut(tween(200)) },
            popEnterTransition = { fadeIn(tween(400)) },
            popExitTransition = { fadeOut(tween(200)) },
        ) {
            HomePage(portrait = portrait, scrollState = scrollState)
        }

        composable(
            route = MainTopic.Pub.route,
            enterTransition = { fadeIn(tween(400)) },
            exitTransition = { fadeOut(tween(200)) },
            popEnterTransition = { fadeIn(tween(400)) },
            popExitTransition = { fadeOut(tween(200)) },
        ) {
            PublicationPage(portrait = portrait)
        }

        composable(
            route = MainTopic.Visual.route,
            enterTransition = { fadeIn(tween(400)) },
            exitTransition = { fadeOut(tween(200)) },
            popEnterTransition = { fadeIn(tween(400)) },
            popExitTransition = { fadeOut(tween(200)) },
        ) {
            VisualRoute()
        }

        composable(
            route = MainTopic.Sim.route,
            enterTransition = { fadeIn(tween(400)) },
            exitTransition = { fadeOut(tween(200)) },
            popEnterTransition = { fadeIn(tween(400)) },
            popExitTransition = { fadeOut(tween(200)) },
        ) {
            SimulationRoute()
        }
    }
}

enum class MainTopic(
    val route: String,
    val label: StringResource,
    val icon: ImageVector,
) {
    Home(
        route = "home",
        label = Res.string.nav_panel_home_label,
        icon  = Icons.Default.Home,
    ),
    Pub(
        route = "pub",
        label = Res.string.nav_panel_publication_label,
        icon = Icons.AutoMirrored.Filled.LibraryBooks,
    ),
    Visual(
        route = "visual",
        label = Res.string.nav_panel_visualization_label,
        icon  = Icons.Default.Gesture,
    ),
    Sim(
        route = "sim",
        label = Res.string.nav_panel_simulation_label,
        icon  = Icons.Outlined.Science,
    )
}