package com.shengj.githubcompose.data.model

import com.google.gson.annotations.SerializedName

/**
 * Represents the content of a README file from a GitHub repository.
 */
data class Readme(
    /** The content of the README file, Base64 encoded. May be null if README doesn't exist or fetching fails. */
    @SerializedName("content") val content: String?
    // Add other fields if needed, like 'encoding', 'size', etc.
) 