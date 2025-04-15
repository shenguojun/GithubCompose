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
        if ((isLoadMore && (!uiState.value.hasMore || uiState.value.isLoadingMore)) || (!isLoadMore && uiState.value.isLoading)) {
            return
        }

        viewModelScope.launch {
            _uiState.value = if (isLoadMore) {
                _uiState.value.copy(isLoadingMore = true)
            } else {
                _uiState.value.copy(isLoading = true, error = null)
            }

            repository.getUserRepos(
                page = if(isLoadMore) uiState.value.page else 1,
                perPage = 20
            ).collect { result ->
                result.onSuccess { repos ->
                    val currentRepos = if (isLoadMore) uiState.value.repositories else emptyList()
                    val nextPage = if (isLoadMore) uiState.value.page + 1 else 2
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        repositories = currentRepos + repos,
                        error = null,
                        hasMore = repos.size == 20,
                        page = nextPage
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

    fun refresh() {
        loadRepositories(isLoadMore = false)
    }

    fun loadMore() {
        loadRepositories(isLoadMore = true)
    }
} 