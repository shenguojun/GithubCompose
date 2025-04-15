package com.shengj.githubcompose.ui.repository

import com.shengj.githubcompose.data.model.Repo

data class RepositoryUiState(
    val isLoading: Boolean = false,
    val repository: Repo? = null,
    val readmeContent: String? = null,
    val error: String? = null
) 