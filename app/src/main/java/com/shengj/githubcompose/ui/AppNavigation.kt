package com.shengj.githubcompose.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.shengj.githubcompose.ui.login.AuthState
import com.shengj.githubcompose.ui.login.AuthViewModel
import com.shengj.githubcompose.ui.login.LoginScreen
import com.shengj.githubcompose.ui.popular.PopularReposScreen
import com.shengj.githubcompose.ui.profile.ProfileScreen
import com.shengj.githubcompose.ui.repository.RepositoryScreen
import com.shengj.githubcompose.ui.search.SearchScreen

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsState()
    val items = listOf(
        BottomNavItem.Popular,
        BottomNavItem.Search,
        BottomNavItem.ProfileNav
    )

    Scaffold(
        bottomBar = {
            BottomNavigation(
                backgroundColor = Color.White,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        selectedContentColor = MaterialTheme.colors.primary,
                        unselectedContentColor = Color.Gray,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.Popular.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(BottomNavItem.Popular.route) {
                    PopularReposScreen(navController = navController)
                }
                composable(BottomNavItem.Search.route) {
                    SearchScreen(navController = navController)
                }
                composable(BottomNavItem.ProfileNav.route) {
                    if (authState is AuthState.Authenticated) {
                        ProfileScreen(
                            navController = navController,
                            authViewModel = authViewModel
                        )
                    } else {
                        LoginScreen()
                    }
                }
                composable("login") {
                    LoginScreen()
                }
                composable(
                    route = "repository/{owner}/{repoName}",
                    arguments = listOf(
                        navArgument("owner") { type = NavType.StringType },
                        navArgument("repoName") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val owner = backStackEntry.arguments?.getString("owner") ?: ""
                    val repoName = backStackEntry.arguments?.getString("repoName") ?: ""
                    RepositoryScreen(
                        owner = owner,
                        repoName = repoName,
                        onNavigateBack = { navController.navigateUp() }
                    )
                }
            }
        }
    }
} 