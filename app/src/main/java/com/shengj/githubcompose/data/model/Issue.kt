package com.shengj.githubcompose.data.model

import com.google.gson.annotations.SerializedName

data class Issue(
    val id: Long,
    val number: Int,
    val title: String,
    val state: String,
    @SerializedName("html_url")
    val htmlUrl: String
    // ...
)