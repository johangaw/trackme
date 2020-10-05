package com.example.trackme

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.ui.test.*
import com.example.trackme.ui.tracks.TrackData
import com.example.trackme.ui.tracks.TracksScreen
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime


class TracksScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val d1: LocalDateTime = LocalDateTime.of(1991, 3, 22, 0, 0)
    private val d2: LocalDateTime = LocalDateTime.of(1990, 4, 20, 0, 0)
    private val trackData = listOf(
        TrackData(1, "Track 1", d1, 3567f, 2.6f),
        TrackData(2, "Track 2", d2, 5067.5f, 1.5f)
    )
    private var lastClicked: TrackData? = null
    private var newClicked = false

    @Before
    fun createComposable() {
        composeTestRule.setContent {
            MaterialTheme {
                TracksScreen(
                    trackData,
                    { lastClicked = it },
                    { newClicked = true }
                )
            }
        }
    }

    @Test
    fun clickingRowsAndAddButtonTriggersCorrectCallback() {
        with(composeTestRule) {
            onNode(containingText("1991-03-22")).performClick()
            assert(trackData[0] == lastClicked)

            onNode(containingText("1990-04-20")).performClick()
            assert(trackData[1] == lastClicked)

            onNode(containingText("new track", true)).performClick()
            Assert.assertTrue(newClicked)
        }
    }

    @Test
    fun eachRowShouldDisplaySpeed() {
        with(composeTestRule) {
            onNode(containingText("2.6 m/s")).assertIsDisplayed()
            onNode(containingText("1.5 m/s")).assertIsDisplayed()
        }
    }

    @Test
    fun eachRowShouldDisplayTotalDistance() {
        with(composeTestRule) {
            onNode(containingText("3.57 km")).assertIsDisplayed()
            onNode(containingText("5.07 km")).assertIsDisplayed()
        }
    }
}

fun containingText(text: String, ignoreCase: Boolean = false): SemanticsMatcher {
    return SemanticsMatcher("Text containing '$text' (ignoreCase: $ignoreCase)") { node ->
        val value = node.config.getOrNull(SemanticsProperties.Text)
        val transformCase = {str: String -> if(ignoreCase) str.toLowerCase() else str }
        value?.text?.let(transformCase)?.contains(text.let(transformCase)) ?: false
    }
}

