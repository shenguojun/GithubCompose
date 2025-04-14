package com.shengj.githubcompose.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shengj.githubcompose.data.GithubRepository
import com.shengj.githubcompose.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

// Define UI state for Profile Screen
data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: GithubRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            repository.getCurrentUser()
                .onStart { _uiState.value = ProfileUiState(isLoading = true) }
                .catch { e -> _uiState.value = ProfileUiState(error = e.message ?: "Unknown error") }
                .collect { result ->
                    result.onSuccess { user ->
                        _uiState.value = ProfileUiState(user = user)
                    }.onFailure { e ->
                        _uiState.value = ProfileUiState(error = e.message ?: "Failed to load profile")
                    }
                }
        }
    }
}