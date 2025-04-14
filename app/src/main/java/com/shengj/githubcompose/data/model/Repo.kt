package com.shengj.githubcompose.data.model

import com.google.gson.annotations.SerializedName

data class Repo(
    val id: Long,
    val name: String,
    @SerializedName("full_name")
    val fullName: String,
    val owner: User,
    val description: String?,
    @SerializedName("stargazers_count")
    val stargazersCount: Int,
    val language: String?,
    @SerializedName("html_url")
    val htmlUrl: String
    // Add other relevant fields
)