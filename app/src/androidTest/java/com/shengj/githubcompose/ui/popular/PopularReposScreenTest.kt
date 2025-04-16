package com.shengj.githubcompose.ui.popular

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shengj.githubcompose.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PopularReposScreenTest {

    // Activity Rule
    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

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

}