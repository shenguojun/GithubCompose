package com.shengj.githubcompose.ui.issue

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

data class IssueDetailUiState(
    val isLoading: Boolean = false,
    val issue: Issue? = null,
    val error: String? = null
)

@HiltViewModel
class IssueDetailViewModel @Inject constructor(
    private val repository: GithubRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IssueDetailUiState())
    val uiState: StateFlow<IssueDetailUiState> = _uiState.asStateFlow()

    fun loadIssueDetail(owner: String, repoName: String, issueNumber: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val issue = repository.getIssueDetail(owner, repoName, issueNumber)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    issue = issue,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "加载议题详情失败"
                )
            }
        }
    }
} 