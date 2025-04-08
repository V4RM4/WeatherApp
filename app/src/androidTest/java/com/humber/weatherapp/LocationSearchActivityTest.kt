package com.humber.weatherapp

import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.activity.ComponentActivity
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertTrue


@RunWith(AndroidJUnit4::class)
class LocationSearchActivityTest {

    private lateinit var scenario: ActivityScenario<LocationSearchActivity>
    private lateinit var searchButton: Button
    private lateinit var searchEditText: EditText
    private lateinit var locationsContainer: LinearLayout
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var scrollView: ScrollView

    @Before
    fun setUp() {
        scenario = ActivityScenario.launch(LocationSearchActivity::class.java)
        scenario.onActivity { activity ->
            searchButton = activity.findViewById(R.id.searchButton)
            searchEditText = activity.findViewById(R.id.searchEditText)
            locationsContainer = activity.findViewById(R.id.locationsContainer)
            bottomNav = activity.findViewById(R.id.bottom_navigation)
            scrollView = activity.findViewById(R.id.scrollview)
        }
    }

    @Test
    fun testSearchButtonVisibility() {
        //if search button is visible
        scenario.onActivity { activity ->
            assert(searchButton.visibility == Button.VISIBLE)
        }
    }

    @Test
    fun testSearchEditTextVisibility() {
        scenario.onActivity { activity ->
            assert(searchEditText.visibility == EditText.VISIBLE)
        }
    }

    @Test
    fun testLocationsContainerVisibility() {
        //locations container
        scenario.onActivity { activity ->
            assert(locationsContainer.visibility == LinearLayout.VISIBLE)
        }
    }

    @Test
    fun testSearchButtonClickWithValidInput() {
        scenario.onActivity { activity ->
            searchEditText.setText("Etobicoke")
            Thread.sleep(1000)
            activity.runOnUiThread {
                searchButton.performClick()
            }
            Thread.sleep(1000)
            // To Wait UI to be idle
            Espresso.onIdle()
            println("Locations Container Child Count: ${locationsContainer.childCount}")
            assert(locationsContainer.childCount > 1)
        }
    }


    @Test
    fun testBottomNavVisibility() {
        scenario.onActivity { activity ->
            assert(bottomNav.visibility == BottomNavigationView.VISIBLE)
        }
    }

    @Test
    fun testScrollViewVisibility() {
        scenario.onActivity { activity ->
            assert(scrollView.visibility == ScrollView.VISIBLE)
        }
    }

    @Test
    fun testNavigationToLocationSearchActivity() {
        scenario.onActivity { activity ->
            activity.runOnUiThread {
                bottomNav.selectedItemId = R.id.nav_locations
            }
            val currentActivity = activity as ComponentActivity
            assertTrue(currentActivity::class.java.simpleName == "LocationSearchActivity")
        }
    }

    @Test
    fun testSearchButtonClickUpdatesUI() {
        scenario.onActivity { activity ->
            activity.runOnUiThread {
                searchEditText.setText("Toronto")
                searchButton.performClick()
                assert(locationsContainer.childCount > 0)
            }
        }
    }
}
