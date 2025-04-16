package com.shengj.githubcompose.data

import com.google.common.truth.Truth.assertThat
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
}
