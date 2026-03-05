package com.socialscreencontrol.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.socialscreencontrol.presentation.auth.AuthScreen
import com.socialscreencontrol.presentation.group.GroupScreen
import com.socialscreencontrol.presentation.home.HomeDashboardScreen
import com.socialscreencontrol.presentation.home.HomeViewModel
import com.socialscreencontrol.presentation.leaderboard.LeaderboardScreen
import com.socialscreencontrol.presentation.limits.LimitsScreen
import com.socialscreencontrol.presentation.request.RequestTimeScreen
import com.socialscreencontrol.presentation.settings.SettingsScreen
import com.socialscreencontrol.presentation.voting.VotingScreen

sealed class Screen(val route: String, val label: String) {
    data object Auth : Screen("auth", "Login")
    data object Home : Screen("home", "Home")
    data object Limits : Screen("limits", "Limits")
    data object Group : Screen("group", "Group")
    data object Request : Screen("request", "Request")
    data object Voting : Screen("voting", "Voting")
    data object Leaderboard : Screen("leaderboard", "Leaderboard")
    data object Settings : Screen("settings", "Settings")
}

@Composable
fun AppNavGraph(homeViewModel: HomeViewModel) {
    val navController = rememberNavController()
    val items = listOf(Screen.Home, Screen.Limits, Screen.Group, Screen.Leaderboard, Screen.Settings)

    Scaffold(
        contentWindowInsets = WindowInsets.navigationBars,
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            AnimatedVisibility(
                visible = currentRoute != Screen.Auth.route,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
            ) {
                NavigationBar {
                    items.forEach { screen ->
                        val icon = when (screen) {
                            Screen.Home -> Icons.Default.Home
                            Screen.Limits -> Icons.Default.Timelapse
                            Screen.Group -> Icons.Default.Group
                            Screen.Leaderboard -> Icons.Default.EmojiEvents
                            else -> Icons.Default.Settings
                        }
                        NavigationBarItem(
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            alwaysShowLabel = false,
                            colors = NavigationBarItemDefaults.colors()
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(navController = navController, startDestination = Screen.Home.route) {
            composable(Screen.Auth.route) { AuthScreen() }
            composable(Screen.Home.route) { HomeDashboardScreen(padding, homeViewModel) }
            composable(Screen.Limits.route) { LimitsScreen(padding) }
            composable(Screen.Group.route) { GroupScreen(padding) }
            composable(Screen.Request.route) { RequestTimeScreen(padding) }
            composable(Screen.Voting.route) { VotingScreen(padding) }
            composable(Screen.Leaderboard.route) { LeaderboardScreen(padding) }
            composable(Screen.Settings.route) { SettingsScreen(padding) }
        }
    }
}
