package com.shengj.githubcompose.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.shengj.githubcompose.ui.login.AppScreen
import com.shengj.githubcompose.ui.login.UserNavigation
import com.shengj.githubcompose.ui.popular.PopularReposScreen
import com.shengj.githubcompose.ui.repository.RepositoryScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem.Popular,
        BottomNavItem.Search, // Add SearchScreen later
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
                        selectedContentColor = MaterialTheme.colors.primary, // Or your desired selected color
                        unselectedContentColor = Color.Gray,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
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
                // Placeholder for Search Screen
                Text("Search Screen Placeholder")
            }
            // Nest the UserNavigation graph (Login/Profile/Repositories/RepoDetail) under the ProfileNav route
            navigation(startDestination = "user_graph_start", route = BottomNavItem.ProfileNav.route) {
                 // Define a starting point within the nested graph if needed, 
                 // or directly use UserNavigation if it handles its own start destination well.
                 // We'll embed UserNavigation directly.
                 composable("user_graph_start") { // This route won't be directly navigated to by the bottom bar
                     UserNavigation() // UserNavigation handles its own internal logic (Login/Profile etc.)
                 }
                 // Add other destinations specific to this nested graph if any, 
                 // but UserNavigation already contains Profile, Repos, RepoDetail.
            }
            
            // Add Repository route
            composable(
                route = AppScreen.Repository.route,
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