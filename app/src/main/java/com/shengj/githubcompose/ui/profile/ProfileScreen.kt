package com.shengj.githubcompose.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.People
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.shengj.githubcompose.data.model.User
import com.shengj.githubcompose.ui.login.auth.AuthViewModel

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel() // Inject AuthViewModel for logout
) {
    val uiState by profileViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                // Add logout button or other actions if needed
                actions = {
                    IconButton(onClick = { authViewModel.logout() }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                    // Add other icons like settings, share from the image if desired
                    // IconButton(onClick = { /* TODO */ }) { Icon(Icons.Default.Share, "Share")}
                    // IconButton(onClick = { /* TODO */ }) { Icon(Icons.Default.Settings, "Settings")}
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
                    UserProfileContent(user = uiState.user!!)
                }
            }
        }
    }
}

@Composable
fun UserProfileContent(user: User) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Make content scrollable
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

        // Bio or Status (Mimicking "Happy codding!")
        // Replace with actual user.bio if available and desired
        InfoRow(icon = Icons.Default.TagFaces, text = user.bio ?: "No bio provided")

        Divider(modifier = Modifier.padding(vertical = 8.dp)) // Separator

        // Other details like Company, Location, Blog, Email
        user.company?.let { InfoRow(icon = Icons.Default.Business, text = it) }
        user.location?.let { InfoRow(icon = Icons.Default.LocationOn, text = it) }
        user.blog?.let { if (it.isNotEmpty()) InfoRow(icon = Icons.Default.Link, text = it) }
        user.email?.let { InfoRow(icon = Icons.Default.Email, text = it) }

        Spacer(modifier = Modifier.height(8.dp))

        // Followers / Following
        // The API might return followers_url/following_url, or counts directly (user.followers, user.following)
        InfoRow(icon = Icons.Default.People, text = "${user.followers ?: 0} followers · ${user.following ?: 0} following")

        Spacer(modifier = Modifier.height(16.dp))

        // TODO: Add sections for Repositories, Organizations, Stars etc. if needed
        // This would likely involve further API calls and navigation

        // Placeholder for achievements/badges seen in the image
        // Text("Achievements / Badges Placeholder")

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


// Preview for UserProfileContent (using dummy data)
@Preview(showBackground = true)
@Composable
fun UserProfileContentPreview() {
    // Define a dummy User object matching your data class structure
    val dummyUser = User(
        login = "shenguojun",
        id = 12345,
        avatarUrl = "https://avatars.githubusercontent.com/u/your_user_id?v=4", // Replace with a valid URL for preview
        name = "Lawrence/中国骏", // Add fields based on your User data class
        company = "Netease Youdao",
        location = "GuangZhou, China",
        blog = "https://shenguojun.github.io/",
        email = "junguoshen@outlook.com",
        bio = "Happy codding!",
        followers = 26, // Add fields if they exist in your User model
        following = 22,// Add fields if they exist in your User model
        // Add other fields your User data class might have (like html_url, etc.)
        htmlUrl = "" // Example of another potential field
    )
    MaterialTheme {
        UserProfileContent(user = dummyUser)
    }
}