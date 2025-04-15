package com.shengj.githubcompose.data.network

import com.shengj.githubcompose.data.model.AccessTokenResponse
import com.shengj.githubcompose.data.model.Issue
import com.shengj.githubcompose.data.model.IssueRequestBody
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

interface GithubApiService {
    // ... other methods like searchRepositories, getUserRepos ...
    // Example: Search Repos
    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc"
    ): Response<SearchResponse> // Define SearchResponse data class

    // Example: Get User Repos (Requires Auth)
    @GET("users/{username}/repos")
    suspend fun getUserRepos(
        @Path("username") username: String,
        @Query("type") type: String = "owner",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("sort") sort: String = "updated"
    ): Response<List<Repo>> // Define Repo data class

    // Example: Create Issue (Requires Auth)
    @POST("repos/{owner}/{repo}/issues")
    suspend fun createIssue(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Body issueRequestBody: IssueRequestBody // Define IssueRequestBody data class
    ): Response<Issue> // Define Issue data class

    // Token exchange endpoint (on github.com, not api.github.com)
    // Use a separate Retrofit instance or provide full URL here
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
    @GET("user")
    suspend fun getCurrentUser(): Response<User> // Assuming User data class exists

    @GET("users/{username}/repos")
    suspend fun getPinnedRepos(
        @Path("username") username: String,
        @Query("sort") sort: String = "pushed",
        @Query("per_page") perPage: Int = 6
    ): Response<List<Repo>>
}