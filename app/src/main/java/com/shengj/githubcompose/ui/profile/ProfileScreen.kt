package com.shengj.githubcompose.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TagFaces
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.shengj.githubcompose.data.model.Repo
import com.shengj.githubcompose.data.model.User
import com.shengj.githubcompose.ui.login.auth.AuthViewModel

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by profileViewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Profile",
                        color = Color.Black
                    ) 
                },
                backgroundColor = Color.White,
                elevation = 0.dp,
                actions = {
                    IconButton(onClick = { authViewModel.logout() }) {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = Color.Black
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colors.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                uiState.user != null -> {
                    UserProfileContent(uiState = uiState)
                }
            }
        }
    }
}

@Composable
fun UserProfileContent(uiState: ProfileUiState) {
    val user = uiState.user!!
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header section (Avatar, Name, Username)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberAsyncImagePainter(user.avatarUrl),
                contentDescription = "${user.login} avatar",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = user.name ?: user.login, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(text = user.login, fontSize = 16.sp, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bio or Status
        user.bio?.let { InfoRow(icon = Icons.Default.TagFaces, text = it) }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Other details like Company, Location, Blog, Email
        user.company?.let { InfoRow(icon = Icons.Default.Business, text = it) }
        user.location?.let { InfoRow(icon = Icons.Default.LocationOn, text = it) }
        user.blog?.let { if (it.isNotEmpty()) InfoRow(icon = Icons.Default.Link, text = it) }
        user.email?.let { InfoRow(icon = Icons.Default.Email, text = it) }

        Spacer(modifier = Modifier.height(8.dp))

        // Followers / Following
        InfoRow(icon = Icons.Default.People, text = "${user.followers ?: 0} followers · ${user.following ?: 0} following")

        Spacer(modifier = Modifier.height(16.dp))

        // Repository Section
        RepositorySection(uiState = uiState)
    }
}

@Composable
fun RepositorySection(uiState: ProfileUiState) {
    Column {
        // Repository Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Repositories",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = { /* TODO: Navigate to repositories list */ }) {
                Text("View All")
            }
        }

        // Pinned Repositories
        if (uiState.pinnedRepos.isNotEmpty()) {
            Text(
                text = "Pinned",
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.pinnedRepos.forEach { repo ->
                    RepositoryCard(repo = repo)
                }
            }
        }
    }
}

@Composable
fun RepositoryCard(repo: Repo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = repo.name,
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold
            )
            
            repo.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.body2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            
            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repo.language?.let {
                    Icon(
                        imageVector = Icons.Default.Circle,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = getLanguageColor(it)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.caption
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
                
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${repo.stargazersCount}",
                    style = MaterialTheme.typography.caption
                )
            }
        }
    }
}

@Composable
private fun getLanguageColor(language: String): Color {
    return when (language.lowercase()) {
        "kotlin" -> Color(0xFF7F52FF)
        "java" -> Color(0xFFB07219)
        "python" -> Color(0xFF3572A5)
        "javascript" -> Color(0xFFF1E05A)
        else -> Color.Gray
    }
}

@Composable
fun InfoRow(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.Gray)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, fontSize = 14.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun UserProfileContentPreview() {
    // Define a dummy User object matching your data class structure
    val dummyUser = User(
        login = "shenguojun",
        id = 12345,
        avatarUrl = "https://avatars.githubusercontent.com/u/your_user_id?v=4",
        name = "Lawrence/中国骏",
        company = "Netease Youdao",
        location = "GuangZhou, China",
        blog = "https://shenguojun.github.io/",
        email = "junguoshen@outlook.com",
        bio = "Happy codding!",
        followers = 26,
        following = 22,
        htmlUrl = ""
    )
    
    // Create dummy UI state with some pinned repos
    val dummyPinnedRepos = listOf(
        Repo(
            id = 1,
            name = "GithubCompose",
            fullName = "shenguojun/GithubCompose",
            owner = dummyUser,
            description = "A GitHub client built with Jetpack Compose",
            stargazersCount = 45,
            language = "Kotlin",
            htmlUrl = "https://github.com/shenguojun/GithubCompose"
        )
    )
    
    val dummyUiState = ProfileUiState(
        isLoading = false,
        user = dummyUser,
        pinnedRepos = dummyPinnedRepos,
        error = null
    )
    
    MaterialTheme {
        UserProfileContent(uiState = dummyUiState)
    }
}