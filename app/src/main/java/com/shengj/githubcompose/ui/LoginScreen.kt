package com.shengj.githubcompose.ui // 替换成你的包名

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shengj.githubcompose.R

// GitHub OAuth URL and Client ID
const val GITHUB_AUTH_URL = "https://github.com/login/oauth/authorize"
const val GITHUB_CLIENT_ID = "Iv23ct4N9yPiZcTSWlno"

@Composable
fun LoginScreen(
    onLoginClick: (Context, String) -> Unit = ::launchCustomTab // 默认启动 Custom Tab
) {
    val context = LocalContext.current
    val authUrl = "$GITHUB_AUTH_URL?client_id=$GITHUB_CLIENT_ID"
    // .appendQueryParameter("scope", "repo,user")
    // .appendQueryParameter("state", "YOUR_RANDOM_STATE_STRING")
    // .appendQueryParameter("redirect_uri", "YOUR_CALLBACK_SCHEME://callback")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black), // Set background to black
        horizontalAlignment = Alignment.CenterHorizontally // Center children horizontally
    ) {
        // Top space for the Logo
        Box(
            modifier = Modifier
                .weight(1f) // Occupy remaining vertical space, pushing the button to the bottom
                .fillMaxWidth(),
            contentAlignment = Alignment.Center // Center the Logo within this Box
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_white),
                contentDescription = "GitHub Logo",
                modifier = Modifier.size(100.dp) // Set Logo size
            )
        }

        // Bottom button area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 32.dp), // Add some padding for the button area
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { onLoginClick(context, authUrl) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White, // Button background white
                    contentColor = Color.Black // Text color black
                )
            ) {
                Text(
                    text = "Login to GITHUB.COM",
                    fontSize = 16.sp // Set font size
                )
            }
        }
    }
}

// 启动 Chrome Custom Tab 的辅助函数
fun launchCustomTab(context: Context, url: String) {
    val customTabsIntent = CustomTabsIntent.Builder()
        // 可以自定义 Custom Tab 的颜色等
        // .setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary))
        .build()
    try {
        customTabsIntent.launchUrl(context, Uri.parse(url))
    } catch (e: Exception) {
        // 处理无法打开 Custom Tab 的情况，例如没有浏览器
        e.printStackTrace()
        // 可以显示一个 Toast 或 Snackbar 提示用户
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000) // 预览背景设为黑色
@Composable
fun LoginScreenPreview() {
    LoginScreen(onLoginClick = { _, _ -> /* 预览中不做任何事 */ })
}