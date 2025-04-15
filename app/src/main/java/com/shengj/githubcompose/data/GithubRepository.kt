package com.shengj.githubcompose.data

// ... other imports ...
import android.util.Log
import com.shengj.githubcompose.BuildConfig
import com.shengj.githubcompose.data.model.Issue
import com.shengj.githubcompose.data.model.IssueRequestBody
import com.shengj.githubcompose.data.model.Repo
import com.shengj.githubcompose.data.model.User
import com.shengj.githubcompose.data.network.GithubApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GithubRepository @Inject constructor(
    private val apiService: GithubApiService
) { // Implement provider if Repo manages token

    private val clientId = BuildConfig.GITHUB_CLIENT_ID // Client ID
    private val clientSecret = BuildConfig.GITHUB_SECRET // add GITHUB_SECRET in local.properties
    private val redirectUri = "shengj://callback" // Must match manifest and GitHub settings

     fun exchangeCodeForToken(code: String): Flow<Result<String>> = flow {
        try {
            val response = apiService.exchangeCodeForToken(
                clientId = clientId,
                clientSecret = clientSecret, // SECURITY RISK
                code = code,
                redirectUri = redirectUri
            )
            if (response.isSuccessful && response.body()?.accessToken != null) {
                val token = response.body()!!.accessToken
                // Save the token immediately
                saveAuthToken(token)
                emit(Result.success(token))
            } else {
                // Log detailed error
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Log.e("GithubRepository", "Token exchange failed: ${response.code()} - $errorBody")
                emit(Result.failure(Exception("Token exchange failed: ${response.code()} - ${response.message()}")))
            }
        } catch (e: Exception) {
            Log.e("GithubRepository", "Token exchange exception", e)
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getCurrentUser(): Flow<Result<User>> = flow {
        val token = getToken()
        if (token == null) {
            emit(Result.failure(Exception("Not authenticated")))
            return@flow
        }
        try {
            // Token should be added by OkHttp Interceptor ideally,
            // or pass explicitly: "token $token"
            val response = apiService.getCurrentUser() // Rely on interceptor preferably
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("API Error getting user: ${response.code()} ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun searchRepos(query: String, language: String?): Flow<Result<List<Repo>>> = flow {
        try {
            // Construct the query string including language if provided
            val searchQuery = buildString {
                append(query)
                if (!language.isNullOrBlank()) {
                    append(" language:$language")
                }
            }
            val response =
                apiService.searchRepositories(query = searchQuery, sort = "stars", order = "desc")
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!.items))
            } else {
                emit(Result.failure(Exception("API Error: ${response.code()} ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e)) // Network error etc.
        }
    }.flowOn(Dispatchers.IO) // Run network call on IO thread

    suspend fun getUserRepos(page: Int = 1, perPage: Int = 20): Flow<Result<List<Repo>>> = flow {
        val token = getToken()
        if (token == null) {
            emit(Result.failure(Exception("Not authenticated")))
            return@flow
        }
        try {
            // 先获取当前用户信息
            val userResponse = apiService.getCurrentUser()
            if (!userResponse.isSuccessful || userResponse.body() == null) {
                emit(Result.failure(Exception("Failed to get current user")))
                return@flow
            }
            
            val username = userResponse.body()!!.login
            val response = apiService.getUserRepos(
                username = username,
                page = page,
                perPage = perPage
            )
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("API Error: ${response.code()} ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun createIssue(
        owner: String,
        repoName: String,
        title: String,
        body: String?
    ): Flow<Result<Issue>> = flow {
        try {
            val requestBody = IssueRequestBody(title, body)
            val response = apiService.createIssue(
                owner,
                repoName,
                requestBody
            ) // Or rely on interceptor
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("API Error: ${response.code()} ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getPinnedRepos(username: String): Flow<Result<List<Repo>>> = flow {
        try {
            val response = apiService.getPinnedRepos(username)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("API Error: ${response.code()} ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    // --- Auth Token Management ---
    fun saveAuthToken(token: String) { DataStoreHelper.saveToken(token) }
    fun clearAuthToken() { DataStoreHelper.clearToken() }
    fun isAuthenticated(): Boolean = DataStoreHelper.getToken() != null
    fun getToken(): String? = DataStoreHelper.getToken()
    // ... other repository methods (getPopularRepos, getRepoDetails)
}