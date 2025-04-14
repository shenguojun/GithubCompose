package com.shengj.githubcompose.data

import com.tencent.mmkv.MMKV

object DataStoreHelper {
    private const val AUTH_TOKEN_KEY = "auth_token"

    fun saveToken(token: String) {
        MMKV.defaultMMKV().encode(AUTH_TOKEN_KEY, token)
    }

    fun getToken(): String? {
        return if (MMKV.defaultMMKV().containsKey(AUTH_TOKEN_KEY)) {
            MMKV.defaultMMKV().decodeString(AUTH_TOKEN_KEY)
        } else null
    }

    fun clearToken() {
        MMKV.defaultMMKV().remove(AUTH_TOKEN_KEY)
    }
}