package com.shengj.githubcompose.data.model

data class IssueRequestBody(
    val title: String,
    val body: String? = null
    // labels, assignees etc. can be added
)