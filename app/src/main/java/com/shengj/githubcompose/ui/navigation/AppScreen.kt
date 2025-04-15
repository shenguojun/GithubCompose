package com.shengj.githubcompose.ui.navigation

sealed class AppScreen(val route: String) {
    object Loading : AppScreen("loading")
    object Login : AppScreen("login")
    object Profile : AppScreen("profile")
    object Repositories : AppScreen("repositories")
    object Repository : AppScreen("repository/{owner}/{repoName}") {
        fun createRoute(owner: String, repoName: String) = "repository/$owner/$repoName"
    }
    object RaiseIssue : AppScreen("repository/{owner}/{repoName}/issues/new") {
        fun createRoute(owner: String, repoName: String) = "repository/$owner/$repoName/issues/new"
    }
    object IssueDetail : AppScreen("repository/{owner}/{repoName}/issues/{issueNumber}") {
        fun createRoute(owner: String, repoName: String, issueNumber: Int) = 
            "repository/$owner/$repoName/issues/$issueNumber"
    }
} 