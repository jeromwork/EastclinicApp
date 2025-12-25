package com.eastclinic.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.waitUntil
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun homeScreen_showsGreeting() {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Добро пожаловать в Eastclinic!").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Добро пожаловать в Eastclinic!").assertIsDisplayed()
    }
}


