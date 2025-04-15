package com.shengj.githubcompose.ui.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shengj.githubcompose.ui.profile.ProfileScreen
import com.shengj.githubcompose.ui.repositories.RepositoriesScreen

@Composable
fun UserNavigation(
    // Obtain AuthViewModel instance (Hilt, Koin, or passed down)
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsState()

    // Determine start destination based on initial auth state check
    // Show a loading screen or splash screen while checking auth state initially
    val startDestination = when (authState) {
        is AuthState.Authenticated -> AppScreen.Profile.route // Go straight to profile if already logged in
        is AuthState.Loading, is AuthState.Unknown -> AppScreen.Loading.route // Show loading initially
        else -> AppScreen.Login.route // Default to login
    }

    // Use LaunchedEffect to navigate once auth state is confirmed after loading/callback
    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            // Navigate to profile and clear the back stack up to login
            navController.navigate(AppScreen.Profile.route) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true } // Clear stack below profile
                launchSingleTop = true // Avoid multiple profile instances
            }
        } else if (authState is AuthState.Unauthenticated) {
            // If user logs out while on profile, navigate back to login
            if (navController.currentDestination?.route == AppScreen.Profile.route) {
                navController.navigate(AppScreen.Login.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(AppScreen.Loading.route) { LoadingScreen() } // Simple loading indicator screen
        composable(AppScreen.Login.route) { LoginScreen() } // Your existing LoginScreen
        composable(AppScreen.Profile.route) { 
            ProfileScreen(
                authViewModel = authViewModel,
                onViewAllRepositories = {
                    navController.navigate(AppScreen.Repositories.route)
                }
            )
        } // New Profile Screen
        composable(AppScreen.Repositories.route) {
            RepositoriesScreen(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        // ... other composables (search, repo details etc)
    }
}

// Define screen routes (example)
sealed class AppScreen(val route: String) {
    object Loading : AppScreen("loading")
    object Login : AppScreen("login")
    object Profile : AppScreen("profile")
    object Repositories : AppScreen("repositories")
    // Add other screens
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator() // Or your custom loading animation
    }
}