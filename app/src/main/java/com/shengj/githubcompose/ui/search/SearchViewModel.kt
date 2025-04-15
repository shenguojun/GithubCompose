package com.shengj.githubcompose.ui.search

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

data class SearchUiState(
    val searchQuery: String = "",
    val selectedLanguage: String? = null,
    val repos: List<Repo> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val page: Int = 1,
    val hasMore: Boolean = true
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: GithubRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun updateSelectedLanguage(language: String?) {
        _uiState.value = _uiState.value.copy(selectedLanguage = language)
    }

    fun searchRepos(isLoadMore: Boolean = false) {
        if (isLoadMore && (!_uiState.value.hasMore || _uiState.value.isLoadingMore)) {
            return
        }

        if (!isLoadMore && _uiState.value.searchQuery.isBlank()) {
            _uiState.value = _uiState.value.copy(
                repos = emptyList(),
                isLoading = false,
                isLoadingMore = false,
                error = null,
                page = 1,
                hasMore = true
            )
            return
        }

        val currentPage = if (isLoadMore) _uiState.value.page else 1

        viewModelScope.launch {
            if (isLoadMore) {
                _uiState.value = _uiState.value.copy(isLoadingMore = true)
            } else {
                _uiState.value = _uiState.value.copy(isLoading = true, page = 1)
            }

            try {
                // Construct the query string including language if provided
                val searchQuery = buildString {
                    append(_uiState.value.searchQuery)
                    if (!_uiState.value.selectedLanguage.isNullOrBlank()) {
                        append(" language:${_uiState.value.selectedLanguage}")
                    }
                }

                repository.searchRepos(searchQuery, currentPage, 20)
                    .collect { result ->
                        result.onSuccess { repos ->
                            val currentRepos = if (isLoadMore) _uiState.value.repos else emptyList()
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isLoadingMore = false,
                                repos = currentRepos + repos,
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
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    error = e.message
                )
            }
        }
    }

    fun loadMore() {
        searchRepos(isLoadMore = true)
    }
} 