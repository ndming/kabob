package com.ndming.kabob

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ndming.kabob.theme.Profile
import com.ndming.kabob.theme.getJetBrainsMonoFamily
import com.ndming.kabob.ui.KabobTopBar
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainPage(
    uiState: MainUiState,
    initialRoute: String,
    onRouteChange: (MainRoute) -> Unit,
    onNavRailVisible: (Boolean) -> Unit,
    currentProfile: Profile = Profile.DARK,
    onProfileChange: (Profile) -> Unit = {},
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            KabobTopBar(
                title = {
                    MainTitle(currentRoute = uiState.currentRoute)
                },
                currentProfile = currentProfile,
                onProfileChange = onProfileChange,
                navigationIcon = {
                    MainTopBarNavigationIcon(hideNavigationRail = uiState.hideNavigation) {
                        onNavRailVisible(!uiState.hideNavigation)
                    }
                }
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                // Navigation rail/panel
                AnimatedVisibility(
                    visible = !uiState.hideNavigation,
                    enter = fadeIn(tween(800, 100)) + expandHorizontally(tween(400), Alignment.Start),
                    exit = fadeOut(tween(300)) + shrinkHorizontally(tween(400), Alignment.Start),
                ) {
                    MainNavigationRail(
                        navController = navController,
                        currentDestination = navBackStackEntry?.destination,
                        onRouteChange = onRouteChange,
                    )
                }

                // Site contents
                MainNavGraph(
                    modifier = Modifier.padding(top = 4.dp, end = 24.dp, start = 24.dp),
                    navController = navController,
                    startDestination = initialRoute,
                )
            }
        }
    }
}

@Composable
private fun MainTitle(
    currentRoute: MainRoute,
    modifier: Modifier = Modifier,
) {
    val titleFontFamily = getJetBrainsMonoFamily()

    BoxWithConstraints(modifier = modifier) {
        if (maxWidth > 180.dp) {
            Row(
                modifier = Modifier.padding(horizontal = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ndming",
                    fontFamily = titleFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                        .copy(alpha = if (this@BoxWithConstraints.maxWidth > 360.dp)  0.6f else 1.0f),
                )

                if (this@BoxWithConstraints.maxWidth > 360.dp) {
                    Text(
                        text = "::",
                        fontFamily = titleFontFamily,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f),
                    )

                    AnimatedContent(
                        targetState = currentRoute,
                        contentAlignment = Alignment.CenterStart,
                        transitionSpec = {
                            val contentEnter = fadeIn(tween(800)) + slideInVertically(tween(600)) { -it / 2 }
                            val contentExit = fadeOut(tween(200)) + slideOutVertically(tween(400)) { it / 2 }
                            contentEnter.togetherWith(contentExit)
                        },
                    ) { route ->
                        Text(
                            modifier = Modifier.requiredWidth(196.dp),
                            fontFamily = titleFontFamily,
                            fontWeight = FontWeight.Medium,
                            text = stringResource(route.title),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MainTopBarNavigationIcon(
    hideNavigationRail: Boolean,
    modifier: Modifier = Modifier,
    onNavRailToggle: () -> Unit,
) {
    // Toggle navigation visibility on/off
    val startPadding by animateDpAsState(
        targetValue = if (hideNavigationRail) 12.dp else 18.dp,
        animationSpec = tween(400)
    )
    IconButton(
        modifier = modifier
            .padding(start = startPadding)
            .pointerHoverIcon(PointerIcon.Hand),
        onClick = onNavRailToggle,
    ) {
        AnimatedContent(
            targetState = hideNavigationRail,
            contentAlignment = Alignment.Center,
            transitionSpec = {
                val contentEnter = fadeIn(tween(800)) + scaleIn(tween(600))
                val contentExit = fadeOut(tween(200)) + scaleOut(tween(400))
                contentEnter.togetherWith(contentExit)
            }
        ) { hideRail ->
            Icon(if (hideRail) Icons.Default.Menu else Icons.Default.Close, null)
        }
    }
}

@Composable
private fun MainNavigationRail(
    navController: NavHostController,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
    onRouteChange: (MainRoute) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(start = 18.dp, end = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MainRoute.entries.forEach { topic ->
            val checked = currentDestination?.hierarchy?.any { it.route == topic.route } == true
            FilledIconToggleButton(
                modifier = Modifier.width(56.dp).pointerHoverIcon(PointerIcon.Hand),
                checked = checked,
                onCheckedChange = {
                    navController.navigate(topic.route) {
                        // Avoid building up a large stack of destinations
                        val startDestId = navController.graph.findStartDestination()
                        popUpTo(startDestId.route!!) { saveState = true }
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                        // Restore state when re-selecting a previously selected item
                        restoreState = true
                    }

                    onRouteChange(topic)
                }
            ) {
                Icon(topic.icon, null)
            }

            Text(
                modifier =  Modifier.padding(top = 4.dp, bottom = 32.dp),
                text = stringResource(topic.label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (checked) 0.8f else 0.4f),
            )
        }
    }
}
