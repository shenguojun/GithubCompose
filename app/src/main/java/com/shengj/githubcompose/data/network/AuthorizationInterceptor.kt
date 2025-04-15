package com.shengj.githubcompose.data.network

import com.shengj.githubcompose.data.DataStoreHelper
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
            .addHeader(name = "Accept", value = "application/vnd.github+json")
        DataStoreHelper.getToken()?.let {
            requestBuilder.addHeader(name = "Authorization", value = "token $it")
        }
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}
