package com.shengj.githubcompose.ui.issues

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shengj.githubcompose.data.GithubRepository
import com.shengj.githubcompose.data.model.Issue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IssuesUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val issues: List<Issue> = emptyList(),
    val error: String? = null,
    val hasMore: Boolean = true,
    val currentPage: Int = 1
)

@HiltViewModel
class IssuesViewModel @Inject constructor(
    private val repository: GithubRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IssuesUiState())
    val uiState: StateFlow<IssuesUiState> = _uiState.asStateFlow()
    
    private val pageSize = 20 // 每页数据量

    fun loadIssues(owner: String, repoName: String, isRefresh: Boolean = false) {
        if (isRefresh) {
            _uiState.value = _uiState.value.copy(
                isRefreshing = true,
                currentPage = 1,
                hasMore = true
            )
        } else {
            if (_uiState.value.isLoading || _uiState.value.isLoadingMore || !_uiState.value.hasMore) {
                return
            }
            _uiState.value = _uiState.value.copy(
                isLoading = _uiState.value.issues.isEmpty(),
                isLoadingMore = _uiState.value.issues.isNotEmpty()
            )
        }

        viewModelScope.launch {
            try {
                val page = if (isRefresh) 1 else _uiState.value.currentPage
                val issues = repository.getIssues(owner, repoName, page, pageSize)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    isRefreshing = false,
                    issues = if (isRefresh) issues else _uiState.value.issues + issues,
                    error = null,
                    hasMore = issues.size >= pageSize,
                    currentPage = page + 1
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    isRefreshing = false,
                    error = e.message ?: "加载议题失败",
                    hasMore = false
                )
            }
        }
    }

    fun loadMore(owner: String, repoName: String) {
        if (!_uiState.value.hasMore || _uiState.value.isLoadingMore) return
        
        loadIssues(owner, repoName)
    }

    fun refresh(owner: String, repoName: String) {
        loadIssues(owner, repoName, true)
    }
} 