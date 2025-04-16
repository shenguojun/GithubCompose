package com.shengj.githubcompose.ui.login

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shengj.githubcompose.MainActivity
import com.shengj.githubcompose.R
import com.shengj.githubcompose.ui.navigation.BottomNavItem
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun loginScreen_displays_when_profileTabClicked_andNotLoggedIn() {
        // Arrange: MainActivity is launched, start destination is Popular.

        // Act: Find and click the 'Profile' bottom navigation item.
        val profileNavLabel = BottomNavItem.ProfileNav.label // Use label from definition
        composeTestRule.onNodeWithText(profileNavLabel).performClick()

        // Assert: LoginScreen elements should now be displayed.
        // We might need waitUntil if there's a slight delay.
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText(composeTestRule.activity.getString(R.string.login_button_text))
                .fetchSemanticsNodes().isNotEmpty()
        }

        val logoDescription = composeTestRule.activity.getString(R.string.login_logo_description)
        val buttonText = composeTestRule.activity.getString(R.string.login_button_text)

        composeTestRule.onNodeWithContentDescription(logoDescription).assertIsDisplayed()
        composeTestRule.onNodeWithText(buttonText).assertIsDisplayed()
    }
} 