package com.shengj.githubcompose.ui.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shengj.githubcompose.data.GithubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepositoryViewModel @Inject constructor(
    private val repository: GithubRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RepositoryUiState())
    val uiState: StateFlow<RepositoryUiState> = _uiState.asStateFlow()

    fun loadRepositoryDetails(owner: String, repoName: String) {
        viewModelScope.launch {
            combine(
                repository.getRepository(owner, repoName).onStart { _uiState.value = _uiState.value.copy(isLoading = true) },
                repository.getReadme(owner, repoName)
            ) { repoResult, readmeResult ->
                Pair(repoResult, readmeResult)
            }.catch { e ->
                // Handle combined error
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to load repository details: ${e.message}")
            }.collect { (repoResult, readmeResult) ->
                val currentError = _uiState.value.error // Keep existing error if any
                val repo = repoResult.getOrNull()
                val readme = readmeResult.getOrNull()
                val repoError = if (repoResult.isFailure) repoResult.exceptionOrNull()?.message else null
                val readmeError = if (readmeResult.isFailure) readmeResult.exceptionOrNull()?.message else null

                var finalError = currentError
                if (repoError != null) {
                    finalError = finalError?.plus("\nRepo Error: $repoError") ?: "Repo Error: $repoError"
                }
                 if (readmeError != null && readmeError != "No README found") { // Ignore 'No README found' as a fatal error
                    finalError = finalError?.plus("\nREADME Error: $readmeError") ?: "README Error: $readmeError"
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    repository = repo,
                    readmeContent = readme?.content,
                    error = finalError?.takeIf { it.isNotEmpty() } // Only set error if there is one
                )
            }
        }
    }
} 