package com.shengj.githubcompose.data.network

import com.shengj.githubcompose.data.model.AccessTokenResponse
import com.shengj.githubcompose.data.model.Issue
import com.shengj.githubcompose.data.model.IssueRequestBody
import com.shengj.githubcompose.data.model.Readme
import com.shengj.githubcompose.data.model.Repo
import com.shengj.githubcompose.data.model.SearchResponse
import com.shengj.githubcompose.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit service interface for interacting with the GitHub API.
 * Defines suspend functions for various API endpoints.
 */
interface GithubApiService {
    // ... other methods like searchRepositories, getUserRepos ...
    // Example: Search Repos
    /**
     * Searches repositories via the GitHub API.
     * See: https://docs.github.com/en/rest/search/search?apiVersion=2022-11-28#search-repositories
     *
     * @param query The search keywords, as well as any qualifiers.
     * @param sort The sort field. Default: results are sorted by best match. Other options: stars, forks, updated.
     * @param order The sort order if sort parameter is provided. One of asc or desc. Default: desc.
     * @param page Page number of the results to fetch.
     * @param perPage The number of results per page (max 100).
     * @return A [Response] containing a [SearchResponse] object.
     */
    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): Response<SearchResponse> // Define SearchResponse data class

    // Example: Get User Repos (Requires Auth)
    /**
     * Lists public repositories for the specified user.
     * See: https://docs.github.com/en/rest/repos/repos?apiVersion=2022-11-28#list-repositories-for-a-user
     *
     * Note: Use [getUserRepositories] to get repositories for the *authenticated* user.
     *
     * @param username The handle for the GitHub user account.
     * @param type Can be one of all, owner, member. Default: owner.
     * @param page Page number of the results to fetch.
     * @param perPage The number of results per page (max 100).
     * @param sort Can be one of created, updated, pushed, full_name. Default: full_name.
     * @return A [Response] containing a list of [Repo] objects.
     */
    @GET("users/{username}/repos")
    suspend fun getUserRepos(
        @Path("username") username: String,
        @Query("type") type: String = "owner", // Consider if this is always desired
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("sort") sort: String = "updated" // Default changed from API default (full_name)
    ): Response<List<Repo>> // Define Repo data class

    // Example: Create Issue (Requires Auth)
    /**
     * Creates an issue on the specified repository. Requires authentication.
     * See: https://docs.github.com/en/rest/issues/issues?apiVersion=2022-11-28#create-an-issue
     *
     * @param owner The account owner of the repository. The name is not case sensitive.
     * @param repo The name of the repository. The name is not case sensitive.
     * @param issueRequestBody The request body containing the title and optional body of the issue.
     * @return A [Response] containing the created [Issue] object.
     */
    @POST("repos/{owner}/{repo}/issues")
    suspend fun createIssue(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Body issueRequestBody: IssueRequestBody // Define IssueRequestBody data class
    ): Response<Issue> // Define Issue data class

    // Token exchange endpoint (on github.com, not api.github.com)
    // Use a separate Retrofit instance or provide full URL here
    /**
     * Exchanges an authorization code for an access token.
     * This request must be made to `github.com`, not `api.github.com`.
     * See: https://docs.github.com/en/apps/oauth-apps/building-oauth-apps/authorizing-oauth-apps#2-users-are-redirected-back-to-your-site-by-github
     *
     * @param clientId The client ID for your GitHub App.
     * @param clientSecret The client secret for your GitHub App.
     * @param code The code you received as a response to Step 1.
     * @param redirectUri The URL in your application where users are sent after authorization.
     * @return A [Response] containing the [AccessTokenResponse].
     */
    @FormUrlEncoded // Important: Send data as form-urlencoded
    @Headers("Accept: application/json") // Request JSON response
    @POST("https://github.com/login/oauth/access_token") // Full URL
    suspend fun exchangeCodeForToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String // Send the same redirect_uri used in auth request
    ): Response<AccessTokenResponse>

    // Endpoint to get authenticated user data
    /**
     * Gets the authenticated user's profile data. Requires authentication.
     * See: https://docs.github.com/en/rest/users/users?apiVersion=2022-11-28#get-the-authenticated-user
     *
     * @return A [Response] containing the [User] object for the authenticated user.
     */
    @GET("user")
    suspend fun getCurrentUser(): Response<User> // Assuming User data class exists

    /**
     * Fetches the 6 most recently pushed repositories for a specified user.
     * Note: This does NOT fetch the user's "Pinned" repositories which require the GraphQL API.
     * Uses the standard list repositories endpoint with specific sorting and pagination.
     * See: https://docs.github.com/en/rest/repos/repos?apiVersion=2022-11-28#list-repositories-for-a-user
     *
     * @param username The handle for the GitHub user account.
     * @return A [Response] containing a list of up to 6 [Repo] objects sorted by push date.
     */
    @GET("users/{username}/repos") // Same endpoint as getUserRepos, different params
    suspend fun getRecentlyPushedRepos(
        @Path("username") username: String,
        @Query("sort") sort: String = "pushed",
        @Query("per_page") perPage: Int = 6
    ): Response<List<Repo>>

    /**
     * Lists repositories for the *authenticated* user. Requires authentication.
     * See: https://docs.github.com/en/rest/repos/repos?apiVersion=2022-11-28#list-repositories-for-the-authenticated-user
     *
     * @param page Page number of the results to fetch.
     * @param perPage The number of results per page (max 100).
     * @return A [Response] containing a list of [Repo] objects.
     */
    @GET("user/repos")
    suspend fun getUserRepositories(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): Response<List<Repo>>

    /**
     * Gets a single repository's details.
     * See: https://docs.github.com/en/rest/repos/repos?apiVersion=2022-11-28#get-a-repository
     *
     * @param owner The account owner of the repository. The name is not case sensitive.
     * @param repo The name of the repository. The name is not case sensitive.
     * @return A [Response] containing the [Repo] object.
     */
    @GET("repos/{owner}/{repo}")
    suspend fun getRepository(@Path("owner") owner: String, @Path("repo") repo: String): Response<Repo>

    /**
     * Gets the README file for a repository.
     * See: https://docs.github.com/en/rest/repos/contents?apiVersion=2022-11-28#get-a-repository-readme
     *
     * @param owner The account owner of the repository. The name is not case sensitive.
     * @param repo The name of the repository. The name is not case sensitive.
     * @return A [Response] containing the [Readme] object (content is Base64 encoded).
     */
    @GET("repos/{owner}/{repo}/readme")
    suspend fun getReadme(@Path("owner") owner: String, @Path("repo") repo: String): Response<Readme>

    /**
     * Lists issues for a repository.
     * See: https://docs.github.com/en/rest/issues/issues?apiVersion=2022-11-28#list-repository-issues
     *
     * @param owner The account owner of the repository. The name is not case sensitive.
     * @param repo The name of the repository. The name is not case sensitive.
     * @param page Page number of the results to fetch.
     * @param perPage The number of results per page (max 100).
     * @return A [Response] containing a list of [Issue] objects.
     */
    @GET("repos/{owner}/{repo}/issues")
    suspend fun getIssues(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): Response<List<Issue>>

    /**
     * Gets a single issue from a repository.
     * See: https://docs.github.com/en/rest/issues/issues?apiVersion=2022-11-28#get-an-issue
     *
     * @param owner The account owner of the repository. The name is not case sensitive.
     * @param repo The name of the repository. The name is not case sensitive.
     * @param issueNumber The number that identifies the issue.
     * @return A [Response] containing the [Issue] object.
     */
    @GET("repos/{owner}/{repo}/issues/{issue_number}")
    suspend fun getIssueDetail(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("issue_number") issueNumber: Int
    ): Response<Issue>
}