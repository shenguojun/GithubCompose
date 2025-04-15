package com.shengj.githubcompose.ui.repositories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shengj.githubcompose.data.GithubRepository
import com.shengj.githubcompose.data.model.Repo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RepositoriesUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val repositories: List<Repo> = emptyList(),
    val error: String? = null,
    val hasMore: Boolean = true,
    val page: Int = 1
)

@HiltViewModel
class RepositoriesViewModel @Inject constructor(
    private val repository: GithubRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RepositoriesUiState(isLoading = true))
    val uiState: StateFlow<RepositoriesUiState> = _uiState.asStateFlow()

    init {
        loadRepositories()
    }

    private fun loadRepositories(isLoadMore: Boolean = false) {
        if (isLoadMore && (!uiState.value.hasMore || uiState.value.isLoadingMore)) {
            return
        }

        viewModelScope.launch {
            if (isLoadMore) {
                _uiState.value = _uiState.value.copy(isLoadingMore = true)
            }

            repository.getUserRepos(
                page = uiState.value.page,
                perPage = 20
            ).collect { result ->
                result.onSuccess { repos ->
                    val currentRepos = if (isLoadMore) uiState.value.repositories else emptyList()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        repositories = currentRepos + repos,
                        error = null,
                        hasMore = repos.size == 20,
                        page = uiState.value.page + 1
                    )
                }.onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun loadMore() {
        loadRepositories(isLoadMore = true)
    }
} 