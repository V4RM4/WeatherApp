package com.humber.weatherapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LauncherActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_launcher)

        auth = Firebase.auth

        // Check authentication state and navigate accordingly
        checkAuthAndNavigate()
    }

    private fun checkAuthAndNavigate() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // User is signed in, check their data
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val userDoc = db.collection("users")
                        .document(currentUser.uid)
                        .get()
                        .await()

                    withContext(Dispatchers.Main) {
                        if (userDoc.exists()) {
                            val savedLocation = userDoc.data?.get("location")
                            if (savedLocation != null) {
                                navigateToHome()
                            } else {
                                navigateToPreferences()
                            }
                        } else {
                            // User document doesn't exist - unusual case
                            Log.w(TAG, "User authenticated but no document exists")
                            navigateToPreferences() // Send to preferences instead of login
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.e(TAG, "Error checking user data", e)
                        Toast.makeText(
                            this@LauncherActivity,
                            "Error checking user data: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        // Even if there's an error, if user is authenticated, send to Home
                        navigateToHome()
                    }
                }
            }
        } else {
            // User is not signed in
            navigateToLogin()
        }
    }

    private fun navigateToHome() {
        startActivity(Intent(this, WeatherActivity::class.java))
        finish()
    }

    private fun navigateToPreferences() {
        startActivity(Intent(this, PreferencesActivity::class.java))
        finish()
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
