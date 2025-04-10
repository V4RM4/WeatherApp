package com.humber.weatherapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.ClearCredentialException
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.humber.weatherapp.databinding.ActivityHomeBinding
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var homeBinding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)

        loadAchievements()

        bottomNav = findViewById(R.id.bottom_navigation)

        // Set the initial selected item
        bottomNav.selectedItemId = R.id.nav_profile

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> {
                    if (this::class.java != HomeActivity::class.java) {
                        startActivity(Intent(this, HomeActivity::class.java))
                    }
                    bottomNav.menu.setGroupCheckable(0, false, true)
                    true
                }
                R.id.nav_locations -> {
                    if (this::class.java != LocationSearchActivity::class.java) {
                        startActivity(Intent(this, LocationSearchActivity::class.java))
                    }
                    bottomNav.menu.setGroupCheckable(0, false, true)
                    true
                }
                R.id.nav_home -> {
                    if (this::class.java != WeatherActivity::class.java) {
                        startActivity(Intent(this, WeatherActivity::class.java))
                    }
                    bottomNav.menu.setGroupCheckable(0, false, true)
                    true
                }
                else -> false
            }
        }

        auth = Firebase.auth
        credentialManager = CredentialManager.create(this)
        val user = auth.currentUser
        if (user != null) {
            homeBinding.welcomeUser.text = "Hello ${user.displayName}"
        }

        homeBinding.signOutBtn.setOnClickListener{
            signOut()
        }

        homeBinding.prefBtn.setOnClickListener{
            val prefIntent = Intent(this, PreferencesActivity::class.java)
            startActivity(prefIntent)
        }
    }

    private fun signOut() {
        // Firebase sign out
        auth.signOut()

        // When a user signs out, clear the current user credential state from all credential providers.
        lifecycleScope.launch {
            try {
                val clearRequest = ClearCredentialStateRequest()
                credentialManager.clearCredentialState(clearRequest)
                val intent = Intent(this@HomeActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
                Toast.makeText(this@HomeActivity, "See you soon!", Toast.LENGTH_LONG).show()
            } catch (e: ClearCredentialException) {
                Log.e(TAG, "Couldn't clear user credentials: ${e.localizedMessage}")
            }
        }
        val dir = filesDir
        dir.deleteRecursively()

        //Clear cache
        cacheDir.deleteRecursively()

    }

    private fun loadAchievements(){
        val pointsHeader = findViewById<TextView>(R.id.pointsHeader)
        val recyclerView = findViewById<RecyclerView>(R.id.achievementsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            pointsHeader.text = "User not authenticated"
            return
        }
        val db = FirebaseFirestore.getInstance()
        val userDocRef = db.collection("users").document(user.uid)
        userDocRef.get().addOnSuccessListener { document ->
            val points = document.getLong("points")?.toInt() ?: 0
            val unlockedAchievements = document.get("unlockedAchievements") as? List<String> ?: emptyList()

            pointsHeader.text = "Total Points: $points"
            val adapter = AchievementsAdapter(DailyRewardManager.achievementsList, unlockedAchievements)
            recyclerView.adapter = adapter
        }.addOnFailureListener {
            pointsHeader.text = "Error loading achievements."
        }
    }
}