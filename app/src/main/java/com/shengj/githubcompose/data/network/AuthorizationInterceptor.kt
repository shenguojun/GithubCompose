package com.shengj.githubcompose.data.network

import com.shengj.githubcompose.data.DataStoreHelper
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request().newBuilder()
      .addHeader(name = "Accept", value = "application/vnd.github+json")
      .addHeader(name = "Authorization", value = "token ${DataStoreHelper.getToken()}")
      .build()
    return chain.proceed(request)
  }
}
