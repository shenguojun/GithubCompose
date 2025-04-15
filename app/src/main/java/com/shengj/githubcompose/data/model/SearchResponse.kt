package com.shengj.githubcompose.data.model

import com.google.gson.annotations.SerializedName

/**
 * Represents the response structure for a GitHub repository search API call.
 */
data class SearchResponse(
    /** The total number of repositories matching the search query. */
    val totalCount: Int,
    /** Indicates whether the search results are incomplete (e.g., due to timeout). */
    @SerializedName("incomplete_results")
    val incompleteResults: Boolean,
    /** The list of repositories found in the search results for the current page. */
    val items: List<Repo>
)