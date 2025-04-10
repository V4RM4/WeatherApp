package com.humber.weatherapp

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onIdle
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocationSearchActivityCardDeleteTest {

    @Test
    fun testAddAndDeleteLocationCard() {
        // Launch the activity
        val scenario = ActivityScenario.launch(LocationSearchActivity::class.java)

        scenario.onActivity { activity ->
            // Get references to views
            val searchEditText = activity.findViewById<EditText>(R.id.searchEditText)
            val searchButton = activity.findViewById<Button>(R.id.searchButton)
            val locationsContainer = activity.findViewById<LinearLayout>(R.id.locationsContainer)

            // Set a test city name
            searchEditText.setText("Toronto")

            // Click search to add the card
            searchButton.performClick()
            Thread.sleep(3000) // wait for the card to be added
            onIdle()

            val initialCount = locationsContainer.childCount
            assertTrue("No cards were added", initialCount > 0)

            // Loop to find the card with tag "TestCity" and delete it
            for (i in 0 until initialCount) {
                val card = locationsContainer.getChildAt(i)
                if (card is CardView && card.tag == "Toronto") {
                    // Search for the delete button using its text ("❌")
                    val viewsWithText = ArrayList<View>()
                    card.findViewsWithText(viewsWithText, "❌", View.FIND_VIEWS_WITH_TEXT)

                    for (view in viewsWithText) {
                        if (view is Button) {
                            view.performClick()
                            Thread.sleep(1000) // give UI time to update
                            break
                        }
                    }
                    break
                }
            }

            // Validate the card was deleted
            val finalCount = locationsContainer.childCount
            assertTrue("The location card was not deleted", finalCount < initialCount)
        }
    }
}
