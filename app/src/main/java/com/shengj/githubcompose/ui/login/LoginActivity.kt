package com.shengj.githubcompose.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// Assuming you use Hilt for ViewModel injection
@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    // Obtain ViewModel instance (via Hilt, Koin, or manual Factory)
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        enableEdgeToEdge()

        // 设置系统栏为浅色模式（深色图标）
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

        setContent {
            // 记住系统UI控制器
            val systemUiController = rememberSystemUiController()

            // 设置状态栏颜色
            SideEffect {
                systemUiController.setStatusBarColor(
                    color = Color.White,
                    darkIcons = true
                )
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                UserNavigation()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    fun handleIntent(intent: Intent?) {
        val uri = intent?.data
        val scheme = uri?.scheme
        val host = uri?.host
        lifecycleScope.launch {
            // Check if this Intent is for our callback scheme/host
            if (scheme == "shengj" && host == "callback") {
                val code = uri.getQueryParameter("code")
                // Optional but recommended: Verify the 'state' parameter here for CSRF protection
                // val state = uri.getQueryParameter("state")
                // if (isValidState(state)) { ... }

                if (code != null) {
                    // Trigger token exchange in ViewModel
                    authViewModel.exchangeCodeForToken(code)
                    // Optional: Show a brief loading message
                    // Toast.makeText(this, "Logging in...", Toast.LENGTH_SHORT).show()
                } else {
                    // Handle error: Code is missing
                    val error = uri.getQueryParameter("error")
                    val errorDesc = uri.getQueryParameter("error_description")
                    Log.e("OAuthCallback", "OAuth Error: $error - $errorDesc")
                    Toast.makeText(this@LoginActivity, "Login failed: ${errorDesc ?: error}", Toast.LENGTH_LONG).show()
                    // Optionally navigate back to login or show an error state
                }
            } else {
                Log.w("OAuthCallback", "Received unexpected intent: $uri")
            }
        }
    }

}