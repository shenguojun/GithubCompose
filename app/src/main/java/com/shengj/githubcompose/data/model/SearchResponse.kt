package com.shengj.githubcompose.data.model

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    val totalCount: Int,
    @SerializedName("incomplete_results")
    val incompleteResults: Boolean,
    val items: List<Repo>
)