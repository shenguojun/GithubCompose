package com.shengj.githubcompose.data.network

import com.shengj.githubcompose.data.DataStoreHelper
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * An OkHttp Interceptor that adds the required `Accept` header and the
 * `Authorization` header with the bearer token (if available) to GitHub API requests.
 */
@Singleton // Mark as Singleton if it should be reused
class AuthorizationInterceptor @Inject constructor(
    private val dataStoreHelper: DataStoreHelper // Inject DataStoreHelper
) : Interceptor {

    /**
     * Intercepts the outgoing request to add headers.
     *
     * Adds `Accept: application/vnd.github+json`.
     * Adds `Authorization: token <token>` if a token is found via [dataStoreHelper].
     *
     * @param chain The interceptor chain.
     * @return The response received after proceeding with the modified request.
     * @throws IOException if the request could not be executed.
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .header("Accept", "application/vnd.github+json") // Use .header() to overwrite if already present

        dataStoreHelper.getToken()?.let {
            requestBuilder.header("Authorization", "token $it") // Use .header() for consistency
        }

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}
