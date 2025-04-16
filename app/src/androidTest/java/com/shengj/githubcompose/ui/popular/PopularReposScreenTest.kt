package com.shengj.githubcompose.ui.popular

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shengj.githubcompose.MainActivity
import com.shengj.githubcompose.data.GithubRepository
import com.shengj.githubcompose.data.model.Issue
import com.shengj.githubcompose.data.model.Readme
import com.shengj.githubcompose.data.model.Repo
import com.shengj.githubcompose.data.model.User
import com.shengj.githubcompose.di.RepositoryModule
import com.shengj.githubcompose.ui.components.ErrorRetryTags
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Singleton

private var shouldFail = false

@HiltAndroidTest
@UninstallModules(RepositoryModule::class)
@RunWith(AndroidJUnit4::class)
class PopularReposScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var fakeRepository: GithubRepository

    @Before
    fun setUp() {
        shouldFail = false
        hiltRule.inject()
    }

    @Test
    fun loadingIndicator_isVisible_whenScreenLaunches() {
        // Arrange: MainActivity is launched, Hilt provides ViewModel, which should initially be loading.

        // Act: Find the loading indicator by its test tag.

        // Assert: Verify the loading indicator is displayed.
        // We might need a short wait if the initial state isn't immediate.
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag(PopularScreenTags.LOADING_INDICATOR)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag(PopularScreenTags.LOADING_INDICATOR).assertIsDisplayed()

        // Also assert that the list is not yet displayed
        composeTestRule.onNodeWithTag(PopularScreenTags.REPO_LIST).assertDoesNotExist()
    }

    @Test
    fun repoList_isDisplayed_whenDataLoadedSuccessfully() {
        // 等待加载完成
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag(PopularScreenTags.REPO_LIST)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // 验证加载指示器消失
        composeTestRule.onNodeWithTag(PopularScreenTags.LOADING_INDICATOR).assertDoesNotExist()
        
        // 验证列表显示
        composeTestRule.onNodeWithTag(PopularScreenTags.REPO_LIST).assertIsDisplayed()
    }

    @Test
    fun errorMessage_isDisplayed_whenLoadingFails() {
        // 等待错误信息显示
        shouldFail = true
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag(ErrorRetryTags.CONTAINER)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // 验证错误重试组件显示
        composeTestRule.onNodeWithTag(ErrorRetryTags.MESSAGE).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ErrorRetryTags.BUTTON).assertIsDisplayed()

        // 验证加载指示器和列表都不显示
        composeTestRule.onNodeWithTag(PopularScreenTags.LOADING_INDICATOR).assertDoesNotExist()
        composeTestRule.onNodeWithTag(PopularScreenTags.REPO_LIST).assertDoesNotExist()
    }

    @Test
    fun loadMoreIndicator_isDisplayed_whenLoadingMoreData() {
        // 等待初始数据加载完成并且列表可见
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag(PopularScreenTags.REPO_LIST)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // 等待第一个仓库项显示
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Repo 0")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // 执行多次滚动操作，确保到达底部
        val list = composeTestRule.onNodeWithTag(PopularScreenTags.REPO_LIST)
        repeat(3) {
            list.performScrollToIndex(19)
            composeTestRule.mainClock.autoAdvance = false
            composeTestRule.mainClock.advanceTimeBy(300)
            composeTestRule.mainClock.autoAdvance = true
        }

        // 等待加载更多指示器显示
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag(PopularScreenTags.LOAD_MORE_INDICATOR)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // 验证加载更多指示器显示
        composeTestRule
            .onNodeWithTag(PopularScreenTags.LOAD_MORE_INDICATOR)
            .assertIsDisplayed()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object TestRepositoryModule {

    @Provides
    @Singleton
    fun provideGithubRepository(): GithubRepository {
        return object : GithubRepository {
            override suspend fun searchPopularRepos(page: Int, perPage: Int): Flow<Result<List<Repo>>> = flow {
                delay(1000) // 模拟网络延迟
                if (shouldFail) {
                    emit(Result.failure(Exception("Failed to load repositories")))
                } else if (page == 1) {
                    val repos = List(20) { index ->
                        Repo(
                            id = index.toLong(),
                            name = "Repo $index",
                            fullName = "Owner/Repo$index",
                            description = "Description $index",
                            owner = User(
                                id = index.toLong(),
                                login = "owner$index",
                                avatarUrl = "https://example.com/avatar$index",
                                name = "Owner $index",
                                company = "Company $index",
                                blog = "https://blog$index.com",
                                location = "Location $index",
                                email = "email$index@example.com",
                                bio = "Bio $index",
                                followers = index * 10,
                                following = index * 5
                            ),
                            stargazersCount = index * 100,
                            forksCount = index * 50,
                            language = "Kotlin",
                            htmlUrl = "https://github.com/owner$index/repo$index"
                        )
                    }
                    emit(Result.success(repos))
                } else {
                    delay(2000) // 模拟加载更多时的网络延迟
                    emit(Result.failure(Exception("Failed to load repositories")))
                }
            }

            override fun exchangeCodeForToken(code: String): Flow<Result<String>> = flow {
                emit(Result.failure(Exception("Not implemented in test")))
            }

            override suspend fun getCurrentUser(): Flow<Result<User>> = flow {
                emit(Result.failure(Exception("Not implemented in test")))
            }

            override suspend fun searchRepos(
                query: String,
                page: Int,
                perPage: Int
            ): Flow<Result<List<Repo>>> = flow {
                emit(Result.failure(Exception("Not implemented in test")))
            }

            override suspend fun getUserRepos(
                page: Int,
                perPage: Int
            ): Flow<Result<List<Repo>>> = flow {
                emit(Result.failure(Exception("Not implemented in test")))
            }

            override suspend fun createIssue(
                owner: String,
                repoName: String,
                title: String,
                body: String?
            ): Flow<Result<Issue>> = flow {
                emit(Result.failure(Exception("Not implemented in test")))
            }

            override suspend fun getRecentlyPushedRepos(username: String): Flow<Result<List<Repo>>> = flow {
                emit(Result.failure(Exception("Not implemented in test")))
            }

            override fun getRepository(owner: String, repoName: String): Flow<Result<Repo>> = flow {
                emit(Result.failure(Exception("Not implemented in test")))
            }

            override suspend fun getReadme(owner: String, repoName: String): Flow<Result<Readme>> = flow {
                emit(Result.failure(Exception("Not implemented in test")))
            }

            override fun saveAuthToken(token: String) {
                // Not implemented in test
            }

            override fun clearAuthToken() {
                // Not implemented in test
            }

            override fun isAuthenticated(): Boolean = false

            override fun getToken(): String? = null

            override suspend fun getIssues(
                owner: String,
                repoName: String,
                page: Int,
                perPage: Int
            ): List<Issue> = emptyList()

            override suspend fun getIssueDetail(
                owner: String,
                repoName: String,
                issueNumber: Int
            ): Issue = throw NotImplementedError("Not implemented in test")
        }
    }
}