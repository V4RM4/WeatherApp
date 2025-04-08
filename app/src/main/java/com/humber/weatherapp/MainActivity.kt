package com.humber.weatherapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.humber.weatherapp.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    lateinit var credentialManager: CredentialManager
    var db = FirebaseFirestore.getInstance()

    // Define the notification permission string
    private val POST_NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS"

    // Request permission launcher
    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d(TAG, "Notification permission granted")
            // You can store this preference in SharedPreferences if needed
        } else {
            Log.d(TAG, "Notification permission denied")
            Toast.makeText(
                this,
                "You won't receive weather notifications without permission",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        credentialManager = CredentialManager.create(this)

        setupGoogleSignIn()

        requestNotificationPermission()
        // Check if user is already signed in
        auth.currentUser?.let {
            redirectUser()
        }
    }

    fun requestNotificationPermission() {
        // For Android 13 and higher, we need to request the POST_NOTIFICATIONS permission
        if (Build.VERSION.SDK_INT >= 33) { // API 33 is Android 13 (Tiramisu)
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                    Log.d(TAG, "Notification permission already granted")
                }
                shouldShowRequestPermissionRationale(POST_NOTIFICATIONS) -> {
                    // Explain why the app needs this permission
                    Toast.makeText(
                        this,
                        "We need permission to send you weather updates and alerts",
                        Toast.LENGTH_LONG
                    ).show()

                    // Then request the permission
                    requestPermissionLauncher.launch(POST_NOTIFICATIONS)
                }
                else -> {
                    // Request the permission directly
                    requestPermissionLauncher.launch(POST_NOTIFICATIONS)
                }
            }
        }
    }


    fun setupGoogleSignIn() {
        // Instantiate a Google sign-in request
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(getString(R.string.default_web_client_id))
            .setFilterByAuthorizedAccounts(false)
            .build()

        // Create the Credential Manager request
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        binding.googleSigninBtn.setOnClickListener {

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val result = credentialManager.getCredential(
                        request = request,
                        context = this@MainActivity
                    )
                    handleSignIn(result.credential)
                } catch (e: GetCredentialException) {
                    Log.e(TAG, "Error getting credential: ${e.message}")
                    Toast.makeText(
                        this@MainActivity,
                        "Sign-in failed: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun handleSignIn(credential: Credential) {
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
        } else {
            Log.w(TAG, "Credential is not of type Google ID!")
            Toast.makeText(
                this,
                "Unsupported credential type",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    protected fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    initializeUser()
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        this,
                        "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    fun initializeUser() {
        val currentUser = auth.currentUser ?: return
        val userRef = db.collection("users").document(currentUser.uid)

        userRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                val userData = hashMapOf(
                    "email" to currentUser.email,
                    "location" to null,
                    "userid" to currentUser.uid,
                    "username" to currentUser.displayName
                )

                userRef.set(userData)
                    .addOnSuccessListener {
                        Log.i(TAG, "User initialized successfully")
                        redirectUser()
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "User initialization error: ${e.message}")
                        Toast.makeText(
                            this,
                            "Failed to initialize user data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                redirectUser()
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, "Document error: ${e.message}")
            Toast.makeText(
                this,
                "Failed to check user data",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun redirectUser() {
        val currentUser = auth.currentUser ?: return

        db.collection("users").document(currentUser.uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val savedLocation = document.data?.get("location")
                    val intent = if (savedLocation == null) {
                        Intent(this, PreferencesActivity::class.java)
                    } else {
                        Intent(this, WeatherActivity::class.java)
                    }
                    startActivity(intent)
                    finish()
                }
            }
            .addOnFailureListener { error ->
                Log.e(TAG, "Error retrieving user data: ${error.message}")
                Toast.makeText(
                    this,
                    "Error retrieving user data: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}
