package com.shengj.githubcompose.ui

import androidx.compose.foundation.layout.padding
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
            BottomNavigation(backgroundColor = Color.White) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        selectedContentColor = MaterialTheme.colors.primary,
                        unselectedContentColor = Color.Gray,
                        onClick = {
                            // 如果点击的是 Profile 且未登录，则跳转到登录页面
                            if (screen == BottomNavItem.ProfileNav && authState !is AuthState.Authenticated) {
                                navController.navigate("login")
                            } else {
                                // 如果当前在登录页面，切换到其他标签时需要清除登录页面
                                if (currentDestination?.route == "login") {
                                    navController.navigate(screen.route) {
                                        popUpTo("login") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                } else {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Popular.route,
            modifier = Modifier.padding(innerPadding)
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