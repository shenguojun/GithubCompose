package com.shengj.githubcompose.data.model

import com.google.gson.annotations.SerializedName

/**
 * Represents the response structure when exchanging an authorization code for an access token.
 */
data class AccessTokenResponse(
    /** The GitHub access token. */
    @SerializedName("access_token")
    val accessToken: String,
    /** The scope of permissions granted by the token (e.g., "repo,user"). Can be null. */
    val scope: String?,
    /** The type of the token (e.g., "bearer"). Can be null. */
    @SerializedName("token_type")
    val tokenType: String?
)