package com.shengj.githubcompose.ui.popular

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

data class PopularReposUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val popularRepos: List<Repo> = emptyList(),
    val error: String? = null,
    val hasMore: Boolean = true,
    val page: Int = 1
)

@HiltViewModel
class PopularReposViewModel @Inject constructor(
    private val repository: GithubRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PopularReposUiState(isLoading = true))
    val uiState: StateFlow<PopularReposUiState> = _uiState.asStateFlow()

    init {
        loadPopularRepos(isRefresh = false)
    }

    private fun loadPopularRepos(isRefresh: Boolean = false, isLoadMore: Boolean = false) {
        if (isLoadMore && (!uiState.value.hasMore || uiState.value.isLoadingMore)) {
            return
        }

        val currentPage = if (isRefresh) 1 else uiState.value.page

        viewModelScope.launch {
            if (isRefresh) {
                _uiState.value = _uiState.value.copy(isLoading = true, page = 1)
            } else if (isLoadMore) {
                _uiState.value = _uiState.value.copy(isLoadingMore = true)
            } else {
                _uiState.value = _uiState.value.copy(isLoading = true)
            }

            repository.searchPopularRepos(page = currentPage, perPage = 20)
                .collect { result ->
                    result.onSuccess { repos ->
                        val currentRepos = if (isRefresh || !isLoadMore) emptyList() else uiState.value.popularRepos
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            popularRepos = currentRepos + repos,
                            error = null,
                            hasMore = repos.size == 20,
                            page = currentPage + 1
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

    fun refreshPopularRepos() {
        loadPopularRepos(isRefresh = true)
    }

    fun loadMorePopularRepos() {
        loadPopularRepos(isLoadMore = true)
    }
} 