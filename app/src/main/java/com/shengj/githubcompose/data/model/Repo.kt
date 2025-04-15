package com.shengj.githubcompose.data.model

import com.google.gson.annotations.SerializedName

/**
 * Represents a GitHub Repository.
 */
data class Repo(
    /** Unique identifier for the repository. */
    val id: Long,
    /** The name of the repository. */
    val name: String,
    /** The full name of the repository, including the owner (e.g., "octocat/Spoon-Knife"). */
    @SerializedName("full_name")
    val fullName: String,
    /** The owner of the repository. */
    val owner: User,
    /** A description of the repository (can be null). */
    val description: String?,
    /** The number of users who have starred the repository. */
    @SerializedName("stargazers_count")
    val stargazersCount: Int,
    /** The number of times the repository has been forked. */
    @SerializedName("forks_count")
    val forksCount: Int,
    /** The primary programming language used in the repository (can be null). */
    val language: String?,
    /** The URL to view the repository on GitHub's website. */
    @SerializedName("html_url")
    val htmlUrl: String
    // Add other relevant fields
)