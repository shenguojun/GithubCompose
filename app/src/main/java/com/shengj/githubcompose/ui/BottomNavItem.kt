package com.shengj.githubcompose.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Popular : BottomNavItem("popular", Icons.Default.Home, "热门")
    object Search : BottomNavItem("search", Icons.Default.Search, "搜索") // Placeholder for Search
    object ProfileNav : BottomNavItem("profile_nav", Icons.Default.Person, "我的") // Wrapper for Profile/Login flow
} 