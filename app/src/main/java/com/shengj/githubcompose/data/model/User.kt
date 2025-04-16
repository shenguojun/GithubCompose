package com.shengj.githubcompose.data.model

import com.google.gson.annotations.SerializedName

/**
 * Represents a GitHub User.
 */
data class User(
    /** The user's login name. */
    val login: String,
    /** The unique identifier for the user. */
    val id: Long,
    /** The URL for the user's avatar image. Can be null. */
    @SerializedName("avatar_url")
    val avatarUrl: String?,
    /** The URL to the user's profile page on GitHub. Can be null. */
    @SerializedName("html_url")
    val htmlUrl: String? = null,
    /** The user's display name. Can be null. */
    val name: String? = null,
    /** The user's company name. Can be null. */
    val company: String? = null,
    /** The URL to the user's blog or website. Can be null. */
    val blog: String? = null,
    /** The user's geographical location. Can be null. */
    val location: String? = null,
    /** The user's publicly visible email address. Can be null. */
    val email: String? = null,
    /** A short biography of the user. Can be null. */
    val bio: String? = null,
    /** The number of followers the user has. Can be null if not requested or available. */
    val followers: Int? = null,
    /** The number of users this user is following. Can be null if not requested or available. */
    val following: Int? = null
    // Add other fields as needed (e.g., public_repos, created_at)
)