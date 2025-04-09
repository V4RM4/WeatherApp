package com.humber.weatherapp

import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PreferencesActivityTest {

    private lateinit var scenario: ActivityScenario<PreferencesActivity>
    private lateinit var cityEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var tvCoordinates: TextView
    private lateinit var continueButton: Button
    private lateinit var scrollView: ScrollView
    private lateinit var feelsLikeCheckBox: CheckBox
    private lateinit var windSpeedCheckBox: CheckBox
    private lateinit var aqiCheckBox: CheckBox

    @Before
    fun setUp() {
        scenario = ActivityScenario.launch(PreferencesActivity::class.java)
        scenario.onActivity { activity ->
            cityEditText = activity.findViewById(R.id.cityEditText)
            searchButton = activity.findViewById(R.id.btnSearch)
            tvCoordinates = activity.findViewById(R.id.tvCoordinates)
            continueButton = activity.findViewById(R.id.btnContinue)
            scrollView = activity.findViewById(R.id.scrollView) // Make sure your root ScrollView has this ID
            feelsLikeCheckBox = activity.findViewById(R.id.cboxFeelsLike)
            windSpeedCheckBox = activity.findViewById(R.id.cboxWindSpeed)
            aqiCheckBox = activity.findViewById(R.id.cboxAQI)
        }
    }

    @Test
    fun testSearchButtonVisible() {
        scenario.onActivity {
            assert(searchButton.visibility == Button.VISIBLE)
        }
    }

    @Test
    fun testCityEditTextVisible() {
        scenario.onActivity {
            assert(cityEditText.visibility == EditText.VISIBLE)
        }
    }

    @Test
    fun testCoordinatesTextViewVisible() {
        scenario.onActivity {
            assert(tvCoordinates.visibility == TextView.VISIBLE)
        }
    }

    @Test
    fun testContinueButtonVisible() {
        scenario.onActivity {
            assert(continueButton.visibility == Button.VISIBLE)
        }
    }

    @Test
    fun testScrollViewVisible() {
        scenario.onActivity {
            assert(scrollView.visibility == ScrollView.VISIBLE)
        }
    }

    @Test
    fun testCheckBoxesVisible() {
        scenario.onActivity {
            assert(feelsLikeCheckBox.visibility == CheckBox.VISIBLE)
            assert(windSpeedCheckBox.visibility == CheckBox.VISIBLE)
            assert(aqiCheckBox.visibility == CheckBox.VISIBLE)
        }
    }

    @Test
    fun testSearchFunctionality() {
        scenario.onActivity { activity ->
            cityEditText.setText("Toronto")
            Thread.sleep(500)
            activity.runOnUiThread {
                searchButton.performClick()
            }
            Thread.sleep(2000)
            Espresso.onIdle()
            assert(tvCoordinates.text.toString().isNotEmpty())
        }
    }
}
