package com.shengj.githubcompose.data

import com.google.common.truth.Truth.assertThat
import com.shengj.githubcompose.data.model.Issue
import com.shengj.githubcompose.data.model.IssueRequestBody
import com.shengj.githubcompose.data.model.Repo
import com.shengj.githubcompose.data.model.SearchResponse
import com.shengj.githubcompose.data.model.User
import com.shengj.githubcompose.data.network.GithubApiService
import io.mockk.every
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.Response

@ExperimentalCoroutinesApi
class GithubRepositoryTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var mockApiService: GithubApiService

    private lateinit var testDispatcher: TestDispatcher

    private lateinit var repository: GithubRepository

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        repository = GithubRepository(mockApiService)
        mockkObject(DataStoreHelper)
    }

    @Test
    fun `getCurrentUser returns failure when not authenticated`() = runTest(testDispatcher) {
        every { DataStoreHelper.getToken() } returns null

        val result = repository.getCurrentUser().first()

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Not authenticated")
        verify(mockApiService, never()).getCurrentUser()
    }

    @Test
    fun `getCurrentUser returns user on successful API call`() = runTest(testDispatcher) {
        val fakeToken = "fake-token"
        val mockUser = User(login = "testuser", id = 1, avatarUrl = "http://example.com/avatar.jpg")
        val successResponse: Response<User> = Response.success(mockUser)

        every { DataStoreHelper.getToken() } returns fakeToken
        whenever(mockApiService.getCurrentUser()).thenReturn(successResponse)

        val result = repository.getCurrentUser().first()

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(mockUser)
        verify(mockApiService).getCurrentUser()
    }

    @Test
    fun `getCurrentUser returns failure on API error`() = runTest(testDispatcher) {
        val fakeToken = "fake-token"
        val errorResponse: Response<User> = Response.error(404, "Not Found".toResponseBody(null))

        every { DataStoreHelper.getToken() } returns fakeToken
        whenever(mockApiService.getCurrentUser()).thenReturn(errorResponse)

        val result = repository.getCurrentUser().first()

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).contains("API Error getting user: 404")
        verify(mockApiService).getCurrentUser()
    }

    @Test
    fun `getCurrentUser returns failure on API exception`() = runTest(testDispatcher) {
        val fakeToken = "fake-token"
        val exception = RuntimeException("Network error")

        every { DataStoreHelper.getToken() } returns fakeToken
        whenever(mockApiService.getCurrentUser()).doSuspendableAnswer { throw exception }

        val result = repository.getCurrentUser().first()

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
        verify(mockApiService).getCurrentUser()
    }

    @Test
    fun `searchRepos returns success with repositories list`() = runTest(testDispatcher) {
        val mockRepos = listOf(
            Repo(
                id = 1,
                name = "repo1",
                fullName = "user1/repo1",
                owner = User(login = "user1", id = 1, avatarUrl = ""),
                description = "desc1",
                stargazersCount = 100,
                forksCount = 50,
                language = "Kotlin",
                htmlUrl = "https://github.com/user1/repo1"
            ),
            Repo(
                id = 2,
                name = "repo2",
                fullName = "user2/repo2",
                owner = User(login = "user2", id = 2, avatarUrl = ""),
                description = "desc2",
                stargazersCount = 200,
                forksCount = 100,
                language = "Java",
                htmlUrl = "https://github.com/user2/repo2"
            )
        )
        val searchResponse = SearchResponse(
            totalCount = 2,
            incompleteResults = false,
            items = mockRepos
        )
        val successResponse: Response<SearchResponse> = Response.success(searchResponse)

        whenever(mockApiService.searchRepositories(
            query = "test",
            sort = "stars",
            order = "desc",
            page = 1,
            perPage = 20
        )).thenReturn(successResponse)

        val result = repository.searchRepos("test").first()

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(mockRepos)
    }

    @Test
    fun `searchPopularRepos returns success with popular repositories`() = runTest(testDispatcher) {
        val mockRepos = listOf(
            Repo(
                id = 1,
                name = "popular1",
                fullName = "user1/popular1",
                owner = User(login = "user1", id = 1, avatarUrl = ""),
                description = "desc1",
                stargazersCount = 1000,
                forksCount = 500,
                language = "Kotlin",
                htmlUrl = "https://github.com/user1/popular1"
            ),
            Repo(
                id = 2,
                name = "popular2",
                fullName = "user2/popular2",
                owner = User(login = "user2", id = 2, avatarUrl = ""),
                description = "desc2",
                stargazersCount = 2000,
                forksCount = 1000,
                language = "Java",
                htmlUrl = "https://github.com/user2/popular2"
            )
        )
        val searchResponse = SearchResponse(
            totalCount = 2,
            incompleteResults = false,
            items = mockRepos
        )
        val successResponse: Response<SearchResponse> = Response.success(searchResponse)

        whenever(mockApiService.searchRepositories(
            query = "stars:>1",
            sort = "stars",
            order = "desc",
            page = 1,
            perPage = 20
        )).thenReturn(successResponse)

        val result = repository.searchPopularRepos().first()

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(mockRepos)
    }

    @Test
    fun `createIssue returns success with created issue`() = runTest(testDispatcher) {
        val mockIssue = Issue(
            id = 1,
            number = 1,
            title = "Test Issue",
            body = "Test Body",
            state = "open",
            createdAt = "2024-03-20T10:00:00Z",
            updatedAt = "2024-03-20T10:00:00Z",
            user = User(login = "user1", id = 1, avatarUrl = "")
        )
        val successResponse: Response<Issue> = Response.success(mockIssue)

        whenever(mockApiService.createIssue(
            owner = "testowner",
            repo = "testrepo",
            issueRequestBody = IssueRequestBody(title = "Test Issue", body = "Test Body")
        )).thenReturn(successResponse)

        val result = repository.createIssue(
            owner = "testowner",
            repoName = "testrepo",
            title = "Test Issue",
            body = "Test Body"
        ).first()

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(mockIssue)
    }

    @Test
    fun `createIssue returns failure on API error`() = runTest(testDispatcher) {
        val errorResponse: Response<Issue> = Response.error(403, "Forbidden".toResponseBody(null))

        whenever(mockApiService.createIssue(
            owner = "testowner",
            repo = "testrepo",
            issueRequestBody = IssueRequestBody(title = "Test Issue", body = "Test Body")
        )).thenReturn(errorResponse)

        val result = repository.createIssue(
            owner = "testowner",
            repoName = "testrepo",
            title = "Test Issue",
            body = "Test Body"
        ).first()

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).contains("API Error: 403")
    }
}
