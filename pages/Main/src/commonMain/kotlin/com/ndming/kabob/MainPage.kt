package com.ndming.kabob

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ndming.kabob.theme.LocalKabobTheme
import com.ndming.kabob.theme.Profile
import com.ndming.kabob.ui.KabobTopBar
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    uiState: MainUiState,
    initialRoute: String,
    onTopicChange: (MainTopic) -> Unit,
    onHideNavPanel: (Boolean) -> Unit,
    onProfileChange: (Profile) -> Unit = {},
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val scrollState = rememberScrollState()

    Surface {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val portrait = maxWidth / maxHeight < 1.0f

            // Close the portrait navigation panel when
            // launching/switching to portrait mode
            LaunchedEffect(portrait) {
                if (portrait && !uiState.hideNavPanel) {
                    onHideNavPanel(true)
                }
            }

            Column(modifier = Modifier.fillMaxSize()) {
                // Top bar
                KabobTopBar(
                    header = {
                        if (!portrait) {
                            MainNavHeader(
                                navController = navController,
                                currentRoute = uiState.currentRoute,
                                onTopicChange = onTopicChange,
                            )
                        }
                    },
                    currentProfile = LocalKabobTheme.current.profile,
                    onProfileChange = onProfileChange,
                    headerIcon = {
                        if (portrait) {
                            IconButton(
                                modifier = Modifier
                                    .padding(start = 12.dp)
                                    .pointerHoverIcon(PointerIcon.Hand),
                                onClick = { onHideNavPanel(!uiState.hideNavPanel) },
                            ) {
                                AnimatedContent(
                                    targetState = uiState.hideNavPanel,
                                    contentAlignment = Alignment.Center,
                                    transitionSpec = {
                                        val contentEnter = fadeIn(tween(600)) + scaleIn(tween(400))
                                        val contentExit = fadeOut(tween(200)) + scaleOut(tween(400))
                                        contentEnter.togetherWith(contentExit)
                                    }
                                ) { hideRail ->
                                    Icon(if (hideRail) Icons.Default.Menu else Icons.Default.Close, null)
                                }
                            }
                        }
                    }
                )

                if (scrollState.value > 0 && uiState.currentRoute == MainTopic.Home) {
                    HorizontalDivider()
                }


                Box {
                    // Site contents
                    MainNavGraph(
                        modifier = Modifier.padding(top = 4.dp),
                        navController = navController,
                        startDestination = initialRoute,
                        portrait = portrait,
                        scrollState = scrollState,
                    )

                    // Overlay nav panel in portrait mode
                    this@Column.AnimatedVisibility(
                        visible = !uiState.hideNavPanel && portrait,
                        enter = fadeIn(tween(400)),
                        exit = fadeOut(tween(600)),
                    ) {
                        Surface(modifier = Modifier.fillMaxSize()) {
                            MainNavPanel(
                                navController = navController,
                                currentDestination = navBackStackEntry?.destination,
                            ) {
                                onTopicChange(it)
                                onHideNavPanel(true)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainNavHeader(
    navController: NavHostController,
    currentRoute: MainTopic,
    modifier: Modifier = Modifier,
    onTopicChange: (MainTopic) -> Unit,
) {
    Row(
        modifier = modifier.padding(start = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MainTopic.entries.forEachIndexed { index, topic ->
            val selected = currentRoute.ordinal == index
            TextButton(
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                onClick = {
                    navController.navigate(topic.route) {
                        // Avoid building up a large stack of destinations
                        val startDstId = navController.graph.findStartDestination()
                        popUpTo(startDstId.route!!) { saveState = true }
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                        // Restore state when re-selecting a previously selected item
                        restoreState = true
                    }

                    onTopicChange(topic)
                }
            ) {
                Text(
                    modifier =  Modifier.padding(horizontal = 4.dp),
                    text = stringResource(topic.label),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = if (selected) 1.0f else 0.3f),
                )
            }

            Spacer(Modifier.width(12.dp))
        }
    }
}

@Composable
private fun MainNavPanel(
    navController: NavHostController,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
    onRouteChange: (MainTopic) -> Unit,
) {
    val selectedColor = MaterialTheme.colorScheme.onPrimaryContainer
    val unselectedColor = MaterialTheme.colorScheme.secondaryContainer

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        MainTopic.entries.forEach { topic ->
            val selected = currentDestination?.hierarchy?.any { it.route == topic.route } == true
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerHoverIcon(PointerIcon.Hand)
                    .clickable {
                        navController.navigate(topic.route) {
                            // Avoid building up a large stack of destinations
                            val startDstId = navController.graph.findStartDestination()
                            popUpTo(startDstId.route!!) { saveState = true }
                            // Avoid multiple copies of the same destination
                            launchSingleTop = true
                            // Restore state when re-selecting a previously selected item
                            restoreState = true
                        }

                        onRouteChange(topic)
                    }
                    .padding(start = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = topic.icon,
                    contentDescription = null,
                    tint = if (selected) selectedColor else unselectedColor,
                )

                Text(
                    modifier =  Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
                    text = stringResource(topic.label),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (selected) selectedColor else unselectedColor,
                )
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
        }
    }
}
