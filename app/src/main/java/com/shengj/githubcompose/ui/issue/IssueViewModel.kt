package com.shengj.githubcompose.ui.issue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shengj.githubcompose.data.GithubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IssueViewModel @Inject constructor(
    private val repository: GithubRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IssueUiState())
    val uiState: StateFlow<IssueUiState> = _uiState.asStateFlow()

    fun createIssue(
        owner: String,
        repo: String,
        title: String,
        body: String?,
        onSuccess: (issueNumber: Int) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.createIssue(owner, repo, title, body)
                .collect { result ->
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    result.fold(
                        onSuccess = { issue ->
                            onSuccess(issue.number)
                        },
                        onFailure = { error ->
                            onError(error.message ?: "创建议题失败")
                        }
                    )
                }
        }
    }
}

data class IssueUiState(
    val isLoading: Boolean = false
) 