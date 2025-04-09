package com.humber.weatherapp

import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class WeatherDetailActivityTest {

    @Test
    fun testActivityLaunch() {
        val scenario = ActivityScenario.launch(WeatherDetailActivity::class.java)
        scenario.onActivity { activity ->
            assertNotNull(activity)
        }
    }

    @Test
    fun testUIComponents() {
        val scenario = ActivityScenario.launch(WeatherDetailActivity::class.java)
        scenario.onActivity { activity ->
            val locationDateTime: TextView = activity.findViewById(R.id.localDateTime)
            val locationName: TextView = activity.findViewById(R.id.locationName)
            assertNotNull(locationDateTime)
            assertNotNull(locationName)
        }
    }
}
