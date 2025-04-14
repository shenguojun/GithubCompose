package com.shengj.githubcompose.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val login: String,
    val id: Long,
    @SerializedName("avatar_url")
    val avatarUrl: String?,
    @SerializedName("html_url")
    val htmlUrl: String?,
    val name: String?,
    val company: String?,
    val blog: String?,
    val location: String?,
    val email: String?,
    val bio: String?,
    val followers: Int?,
    val following: Int?
    // Add other fields as needed (e.g., public_repos, created_at)
)