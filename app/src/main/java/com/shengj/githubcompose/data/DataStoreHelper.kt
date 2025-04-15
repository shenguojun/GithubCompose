package com.shengj.githubcompose.data

import com.tencent.mmkv.MMKV

/**
 * A helper object for managing persistent data storage using MMKV.
 * Currently used for storing and retrieving the GitHub authentication token.
 */
object DataStoreHelper {
    private const val AUTH_TOKEN_KEY = "auth_token"

    /**
     * Saves the GitHub authentication token to MMKV.
     *
     * @param token The token string to save.
     */
    fun saveToken(token: String) {
        MMKV.defaultMMKV().encode(AUTH_TOKEN_KEY, token)
    }

    /**
     * Retrieves the saved GitHub authentication token from MMKV.
     *
     * @return The saved token string, or null if no token is found.
     */
    fun getToken(): String? {
        return MMKV.defaultMMKV().decodeString(AUTH_TOKEN_KEY, null)
    }

    /**
     * Removes the saved GitHub authentication token from MMKV.
     */
    fun clearToken() {
        MMKV.defaultMMKV().remove(AUTH_TOKEN_KEY)
    }
}