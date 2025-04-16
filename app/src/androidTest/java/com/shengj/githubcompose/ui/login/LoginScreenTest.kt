package com.shengj.githubcompose.ui.login

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.intent.matcher.UriMatchers.hasHost
import androidx.test.espresso.intent.matcher.UriMatchers.hasPath
import androidx.test.espresso.intent.rule.IntentsRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shengj.githubcompose.MainActivity
import com.shengj.githubcompose.R
import com.shengj.githubcompose.ui.navigation.BottomNavItem
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    // Chain the rules: IntentsRule needs to wrap the Activity rule
    @get:Rule
    val intentsRule = IntentsRule()

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

    @Test
    fun loginButton_launchesCorrectIntent() {
        // Arrange: Navigate to the Login Screen
        val profileNavLabel = BottomNavItem.ProfileNav.label
        composeTestRule.onNodeWithText(profileNavLabel).performClick()

        // Ensure Login Button is present before clicking
        val buttonText = composeTestRule.activity.getString(R.string.login_button_text)
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText(buttonText)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText(buttonText).assertIsDisplayed() // Double check

        // Stub the intent to prevent it from actually launching the browser
        Intents.intending(hasAction(Intent.ACTION_VIEW))
            .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))

        // Act: Click the login button
        composeTestRule.onNodeWithText(buttonText).performClick()

        // Assert: Verify that an intent to the GitHub auth URL was sent
        Intents.intended(allOf(
            hasAction(Intent.ACTION_VIEW),
            hasData(hasHost("github.com")),
            hasData(hasPath("/login/oauth/authorize"))
        ))
    }
} 