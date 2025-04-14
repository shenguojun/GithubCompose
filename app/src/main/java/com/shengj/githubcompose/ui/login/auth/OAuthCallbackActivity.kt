package com.shengj.githubcompose.ui.login.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint // Or your DI setup
import kotlinx.coroutines.launch

// Assuming you use Hilt for ViewModel injection
@AndroidEntryPoint
class OAuthCallbackActivity : ComponentActivity() {

    // Obtain ViewModel instance (via Hilt, Koin, or manual Factory)
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val uri = intent?.data
        val scheme = uri?.scheme
        val host = uri?.host

        // Check if this Intent is for our callback scheme/host
        if (scheme == "shengj" && host == "callback") {
            val code = uri.getQueryParameter("code")
            // Optional but recommended: Verify the 'state' parameter here for CSRF protection
            // val state = uri.getQueryParameter("state")
            // if (isValidState(state)) { ... }

            if (code != null) {
                // Trigger token exchange in ViewModel
                lifecycleScope.launch { // Use lifecycleScope for Activity context
                    authViewModel.exchangeCodeForToken(code)
                    finish()
                }
                // Optional: Show a brief loading message
                // Toast.makeText(this, "Logging in...", Toast.LENGTH_SHORT).show()
            } else {
                // Handle error: Code is missing
                val error = uri.getQueryParameter("error")
                val errorDesc = uri.getQueryParameter("error_description")
                Log.e("OAuthCallback", "OAuth Error: $error - $errorDesc")
                Toast.makeText(this, "Login failed: ${errorDesc ?: error}", Toast.LENGTH_LONG).show()
                // Optionally navigate back to login or show an error state
                finish()
            }
        } else {
            Log.w("OAuthCallback", "Received unexpected intent: $uri")
            finish()
        }
    }
}