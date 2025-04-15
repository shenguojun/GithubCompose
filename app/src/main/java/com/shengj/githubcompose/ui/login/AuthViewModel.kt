package com.shengj.githubcompose.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shengj.githubcompose.data.GithubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Define possible authentication states
sealed class AuthState {
    object Unknown : AuthState()         // Initial state before checking
    object Unauthenticated : AuthState() // Checked, definitely not logged in
    object Authenticated : AuthState()   // Logged in successfully
    object Loading : AuthState()         // In progress (e.g., exchanging token)
    data class Error(val message: String) : AuthState() // An error occurred
}

@HiltViewModel // Or your DI annotation
class AuthViewModel @Inject constructor(
    private val repository: GithubRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unknown)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkInitialAuthState()
    }

    private fun checkInitialAuthState() {
        // Check DataStore on startup
        viewModelScope.launch {
            if (repository.isAuthenticated()) {
                _authState.value = AuthState.Authenticated
            } else {
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    // Called by OAuthCallbackActivity
    suspend fun exchangeCodeForToken(code: String) {
        _authState.value = AuthState.Loading // Indicate loading state
        repository.exchangeCodeForToken(code)
            .collect { result ->
                result.onSuccess { token ->
                    _authState.value = AuthState.Authenticated
                    Log.i("AuthViewModel", "Token received and stored successfully.")
                }.onFailure { e ->
                    _authState.value = AuthState.Error("Login failed: ${e.message}")
                    Log.e("AuthViewModel", "Token exchange failed", e)
                }
            }
    }

    fun logout() {
        viewModelScope.launch {
            repository.clearAuthToken()
            _authState.value = AuthState.Unauthenticated
        }
    }
}