package com.ndming.kabob

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import com.ndming.kabob.composeblog.generated.resources.Res
import com.ndming.kabob.composeblog.generated.resources.github
import com.ndming.kabob.composeblog.generated.resources.linkedin
import com.ndming.kabob.sitemap.KabobNavGraph
import com.ndming.kabob.sitemap.KabobTopic
import com.ndming.kabob.theme.KabobTheme
import com.ndming.kabob.theme.LocalKabobTheme
import com.ndming.kabob.theme.Profile
import kotlinx.browser.window
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun KabobApp(
    uiState: KabobUiState,
    initialRoute: String,
    onProfileChange: (Profile) -> Unit,
    onNavRailChange: (Boolean) -> Unit,
    onTopicChange: (KabobTopic) -> Unit,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val kabobTheme = KabobTheme(uiState.currentProfile, uiState.currentConcept)

    val drawerState = rememberDrawerState(DrawerValue.Open)
    val coroutineScope = rememberCoroutineScope()

    CompositionLocalProvider(LocalKabobTheme provides kabobTheme) {
        KabobTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    KabobTopBar(
                        hideNavigationRail = uiState.hideNavigation,
                        currentProfile = uiState.currentProfile,
                        onNavRailToggle = {
                            onNavRailChange(!uiState.hideNavigation)
                            coroutineScope.launch {
                                if (uiState.hideNavigation) {
                                    drawerState.open()
                                } else {
                                    drawerState.close()
                                }
                            }
                        },
                        onProfileToggle = {
                            if (uiState.currentProfile == Profile.LIGHT) {
                                onProfileChange(Profile.DARK)
                            } else {
                                onProfileChange(Profile.LIGHT)
                            }
                        },
                    )

                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Navigation rail/panel
                        AnimatedVisibility(
                            visible = !uiState.hideNavigation,
                            enter = fadeIn(tween(800, 100)) + expandHorizontally(tween(400), Alignment.Start),
                            exit = fadeOut(tween(300)) + shrinkHorizontally(tween(400), Alignment.Start),
                        ) {
                            KabobNavigationRail(
                                navController = navController,
                                currentDestination = navBackStackEntry?.destination,
                                onTopicChange = onTopicChange,
                            )
                        }

                        // Site contents
                        KabobNavGraph(
                            modifier = Modifier.padding(top = 4.dp, end = 24.dp, start = 24.dp),
                            navController = navController,
                            startDestination = initialRoute,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KabobTopBar(
    hideNavigationRail: Boolean,
    currentProfile: Profile,
    modifier: Modifier = Modifier,
    onProfileToggle: () -> Unit,
    onNavRailToggle: () -> Unit,
) {
    TopAppBar(
        modifier = modifier.fillMaxWidth(),
        title = { Text("ndming", Modifier.padding(horizontal = 24.dp)) },
        navigationIcon = {
            // Toggle navigation visibility on/off
            IconButton(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
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
                    Icon(if (hideRail) Icons.Default.Menu else Icons.Default.ArrowBackIosNew, null)
                }
            }
        },
        actions = {
            // Light/dark mode button
            IconButton(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .pointerHoverIcon(PointerIcon.Hand),
                onClick = onProfileToggle
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
                onClick = { window.open("https://github.com/ndming", "_blank") }
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
                onClick = { window.open("https://www.linkedin.com/in/minh-nguyen-59671b194/", "_blank") }
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
                onClick = { window.open("mailto:ndminh1101@gmail.com", "_blank") }
            ) {
                Icon(Icons.Default.Mail, null)
            }
        }
    )
}

@Composable
private fun KabobNavigationRail(
    navController: NavHostController,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
    onTopicChange: (KabobTopic) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(start = 18.dp, end = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        KabobTopic.entries.forEach { topic ->
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

                    onTopicChange(topic)
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
