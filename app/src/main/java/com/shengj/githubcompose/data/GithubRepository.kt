package com.shengj.githubcompose.data

// ... other imports ...
import android.util.Log
import com.shengj.githubcompose.BuildConfig
import com.shengj.githubcompose.data.model.Issue
import com.shengj.githubcompose.data.model.IssueRequestBody
import com.shengj.githubcompose.data.model.Readme
import com.shengj.githubcompose.data.model.Repo
import com.shengj.githubcompose.data.model.User
import com.shengj.githubcompose.data.network.GithubApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Repository class for interacting with the GitHub API.
 * Handles data fetching, caching (if implemented), and authentication token management.
 */
class GithubRepository @Inject constructor(
    private val apiService: GithubApiService
) { // Consider dependency injection for token management if needed

    private val clientId = BuildConfig.GITHUB_CLIENT_ID // GitHub App Client ID
    // IMPORTANT: Ensure GITHUB_SECRET in local.properties is kept secure and not committed to VCS.
    private val clientSecret = BuildConfig.GITHUB_SECRET // GitHub App Client Secret
    private val redirectUri = "shengj://callback" // Must match AndroidManifest and GitHub App settings

    /**
     * Exchanges an authorization code for an access token using the GitHub OAuth flow.
     *
     * @param code The authorization code received from the GitHub callback.
     * @return A Flow emitting the Result containing the access token on success, or an exception on failure.
     */
     fun exchangeCodeForToken(code: String): Flow<Result<String>> = flow {
        try {
            val response = apiService.exchangeCodeForToken(
                clientId = clientId,
                clientSecret = clientSecret, // Handled via BuildConfig, ensure local.properties is secure
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

    /**
     * Fetches the currently authenticated user's profile information.
     *
     * Relies on [AuthorizationInterceptor] to add the necessary authentication header.
     * @return A Flow emitting the Result containing the [User] object on success, or an exception on failure.
     */
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

    /**
     * Searches for repositories on GitHub based on a query.
     *
     * @param query The search query string.
     * @param page The page number for pagination (default: 1).
     * @param perPage The number of results per page (default: 20).
     * @return A Flow emitting the Result containing a list of [Repo] objects on success, or an exception on failure.
     */
    suspend fun searchRepos(query: String, page: Int = 1, perPage: Int = 20): Flow<Result<List<Repo>>> = flow {
        try {
            val response = apiService.searchRepositories(
                query = query,
                sort = "stars",
                order = "desc",
                page = page,
                perPage = perPage
            )
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!.items))
            } else {
                emit(Result.failure(Exception("API Error: ${response.code()} ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Searches for popular repositories on GitHub (query hardcoded to 'stars:>1').
     *
     * @param page The page number for pagination (default: 1).
     * @param perPage The number of results per page (default: 20).
     * @return A Flow emitting the Result containing a list of popular [Repo] objects on success, or an exception on failure.
     */
    suspend fun searchPopularRepos(page: Int = 1, perPage: Int = 20): Flow<Result<List<Repo>>> = flow {
        try {
            val response =
                apiService.searchRepositories(query = "stars:>1", sort = "stars", order = "desc", page = page, perPage = perPage)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!.items))
            } else {
                emit(Result.failure(Exception("API Error: ${response.code()} ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e)) // Network error etc.
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Fetches the repositories belonging to the currently authenticated user.
     *
     * Note: This implementation currently fetches the user profile first to get the username.
     * Consider optimizing by directly calling the user repos endpoint if the API and interceptor support it.
     *
     * @param page The page number for pagination (default: 1).
     * @param perPage The number of results per page (default: 20).
     * @return A Flow emitting the Result containing a list of the user's [Repo] objects on success, or an exception on failure.
     */
    suspend fun getUserRepos(page: Int = 1, perPage: Int = 20): Flow<Result<List<Repo>>> = flow {
        val token = getToken()
        if (token == null) {
            emit(Result.failure(Exception("Not authenticated")))
            return@flow
        }
        try {
            // Fetch current user info first to get username (potential optimization: directly call user repos endpoint)
            val userResponse = apiService.getCurrentUser()
            if (!userResponse.isSuccessful || userResponse.body() == null) {
                emit(Result.failure(Exception("Failed to get current user profile before fetching repos")))
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

    /**
     * Creates a new issue in a specified repository.
     *
     * @param owner The owner of the repository.
     * @param repoName The name of the repository.
     * @param title The title of the new issue.
     * @param body The optional body content of the issue.
     * @return A Flow emitting the Result containing the created [Issue] object on success, or an exception on failure.
     */
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
            ) // Relies on interceptor for auth
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("API Error: ${response.code()} ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Fetches the 6 most recently pushed repositories for a specified user.
     * Note: This does NOT fetch the user's "Pinned" repositories which require the GraphQL API.
     * Uses the standard list repositories endpoint with specific sorting and pagination.
     * See: https://docs.github.com/en/rest/repos/repos?apiVersion=2022-11-28#list-repositories-for-a-user
     *
     * @param username The handle for the GitHub user account.
     * @return A Flow emitting the Result containing a list of up to 6 [Repo] objects sorted by push date.
     */
    suspend fun getRecentlyPushedRepos(username: String): Flow<Result<List<Repo>>> = flow {
        try {
            // Call the renamed function in the ApiService
            val response = apiService.getRecentlyPushedRepos(username)
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
    /** Saves the authentication token using [DataStoreHelper]. */
    fun saveAuthToken(token: String) { DataStoreHelper.saveToken(token) }
    /** Clears the authentication token using [DataStoreHelper]. */
    fun clearAuthToken() { DataStoreHelper.clearToken() }
    /** Checks if an authentication token exists using [DataStoreHelper]. */
    fun isAuthenticated(): Boolean = DataStoreHelper.getToken() != null
    /** Retrieves the authentication token using [DataStoreHelper]. */
    fun getToken(): String? = DataStoreHelper.getToken()

    /**
     * Fetches details for a single repository.
     *
     * @param owner The owner of the repository.
     * @param repoName The name of the repository.
     * @return A Flow emitting the Result containing the [Repo] object on success, or an exception on failure.
     */
    fun getRepository(owner: String, repoName: String): Flow<Result<Repo>> = flow {
        try {
            val response = apiService.getRepository(owner, repoName)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Error fetching repository: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Fetches the README content for a specific repository.
     *
     * @param owner The owner of the repository.
     * @param repoName The name of the repository.
     * @return A Flow emitting the Result containing the [Readme] object on success, or an exception on failure
     *         (including a specific failure for '404 Not Found').
     */
    fun getReadme(owner: String, repoName: String): Flow<Result<Readme>> = flow {
        try {
            val response = apiService.getReadme(owner, repoName)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else if (response.code() == 404) {
                 emit(Result.failure(Exception("No README found"))) // Specific handling for 404
            } else {
                emit(Result.failure(Exception("Error fetching README: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Fetches a list of issues for a specific repository.
     *
     * Note: This function runs on Dispatchers.IO and throws an exception on failure,
     * unlike other methods returning Flow<Result<T>>.
     *
     * @param owner The owner of the repository.
     * @param repoName The name of the repository.
     * @param page The page number for pagination (default: 1).
     * @param perPage The number of results per page (default: 20).
     * @return A list of [Issue] objects.
     * @throws Exception if the API call fails.
     */
    suspend fun getIssues(owner: String, repoName: String, page: Int = 1, perPage: Int = 20): List<Issue> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getIssues(owner, repoName, page, perPage)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                throw Exception("Failed to fetch issues: ${response.code()} - $errorBody")
            }
        }
    }

    /**
     * Fetches the details for a single issue.
     *
     * Note: This function runs on Dispatchers.IO and throws an exception on failure,
     * unlike other methods returning Flow<Result<T>>.
     *
     * @param owner The owner of the repository.
     * @param repoName The name of the repository.
     * @param issueNumber The number of the issue to fetch.
     * @return The [Issue] object containing the details.
     * @throws Exception if the API call fails or the issue is not found.
     */
    suspend fun getIssueDetail(owner: String, repoName: String, issueNumber: Int): Issue {
        return withContext(Dispatchers.IO) {
            val response = apiService.getIssueDetail(owner, repoName, issueNumber)
            if (response.isSuccessful && response.body() != null) {
                 response.body()!!
            } else {
                 val errorBody = response.errorBody()?.string() ?: "Unknown error"
                 throw Exception("Failed to fetch issue detail: ${response.code()} - $errorBody")
            }
        }
    }
}