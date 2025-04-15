package com.shengj.githubcompose.data.model

/**
 * Represents the request body structure for creating a new GitHub Issue.
 */
data class IssueRequestBody(
    /** The title of the issue. */
    val title: String,
    /** The optional body content of the issue. Defaults to null. */
    val body: String? = null
    // labels, assignees etc. can be added
)