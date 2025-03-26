package com.humber.weatherapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_launcher)
    }

    override fun onStart() {
        val auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser != null) {
            super.onStart()
            val database = FirebaseFirestore.getInstance()
            val userRef = database.collection("users").document("${currentUser?.uid}")
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val userData = document.data
                        val savedLocation = userData?.get("location")
                        if (savedLocation == null) {
                            val prefIntent = Intent(this, PreferencesActivity::class.java)
                            startActivity(prefIntent)
                            finish()
                        } else {
                            val homeIntent = Intent(this, HomeActivity::class.java)
                            startActivity(homeIntent)
                            finish()
                        }
                    }
                }
                .addOnFailureListener { error ->
                    Toast.makeText(
                        this,
                        "Error retrieving document: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        } else {
            val loginIntent = Intent(this, MainActivity::class.java)
            startActivity(loginIntent)
            finish()
        }
    }
}