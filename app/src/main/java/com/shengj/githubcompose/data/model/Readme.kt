package com.shengj.githubcompose.data.model

import com.google.gson.annotations.SerializedName

data class Readme(
    @SerializedName("content") val content: String? // Base64 encoded content
    // Add other fields if needed, like 'encoding', 'size', etc.
) 