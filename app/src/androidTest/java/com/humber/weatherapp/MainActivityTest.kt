package com.humber.weatherapp

import android.widget.Button
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.button.MaterialButton
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private lateinit var scenario: ActivityScenario<MainActivity>
    private lateinit var googleSignInButton: Button

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            googleSignInButton = activity.findViewById(R.id.googleSigninBtn)
        }
    }

    @Test
    fun testGoogleSignInButtonVisible() {
        assertEquals(Button.VISIBLE, googleSignInButton.visibility)
        assertTrue(googleSignInButton.isEnabled)
    }
}
