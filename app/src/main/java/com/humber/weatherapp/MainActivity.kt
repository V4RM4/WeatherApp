package com.humber.weatherapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
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

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        auth = Firebase.auth
        credentialManager = CredentialManager.create(this)

        // Instantiate a Google sign-in request
        val googleIdOption = GetGoogleIdOption.Builder()
            // This is from the json which we later added into strings
            .setServerClientId(getString(R.string.default_web_client_id))
            // Don't only show accounts previously used to sign in.
            .setFilterByAuthorizedAccounts(false)
            .build()

        // Create the Credential Manager request
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()


        mainBinding.googleSigninBtn.setOnClickListener {
            // getCredential only works within a coroutine
            CoroutineScope(Dispatchers.Main).launch {
                val result = credentialManager.getCredential(
                    request = request,
                    context = this@MainActivity
                )
                handleSignIn(result.credential)
            }
        }
    }

    private fun handleSignIn(credential: Credential) {
        // Check if credential is of type Google ID
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            // Create Google ID Token
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

            // Sign in to Firebase with using the token
            firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
        } else {
            Log.w(TAG, "Credential is not of type Google ID!")
        }
    }

//    override fun onStart() {
//        val currentUser = auth.currentUser
//        if (currentUser != null){
//            super.onStart()
//            val intent = Intent(this, HomeActivity::class.java)
//            startActivity(intent)
//        }
//    }

//    override fun onStart() {
//        val currentUser = auth.currentUser
//        if (currentUser != null) {
//            super.onStart()
//            val database = FirebaseFirestore.getInstance()
//            val userRef = database.collection("users").document("${currentUser?.uid}")
//            userRef.get()
//                .addOnSuccessListener { document ->
//                    if (document.exists()) {
//                        val userData = document.data
//                        val savedLocation = userData?.get("location")
//                        if (savedLocation == null) {
//                            val prefIntent = Intent(this, PreferencesActivity::class.java)
//                            startActivity(prefIntent)
//                            finish()
//                        } else {
//                            val homeIntent = Intent(this, HomeActivity::class.java)
//                            startActivity(homeIntent)
//                            finish()
//                        }
//                    }
//                }
//                .addOnFailureListener { error ->
//                    Toast.makeText(
//                        this,
//                        "Error retrieving document: ${error.message}",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//        }
//    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Log.d(TAG, "signInWithCredential:success")
                    initializeUser()
                    redirectUser()
                } else {
                    // If sign in fails, display a message to the user
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun initializeUser(){
        val currentUser = auth.currentUser
        val database = FirebaseFirestore.getInstance()
        val userRef = database.collection("users").document("${currentUser?.uid}")
        userRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                val userData = hashMapOf(
                    "email" to currentUser?.email,
                    "location" to null,
                    "userid" to currentUser?.uid,
                    "username" to currentUser?.displayName
                )
                userRef.set(userData)
                    .addOnSuccessListener {
                        Log.i("success", "User initialised successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.e("initialization error", e.toString())
                    }
            }
        }
            .addOnFailureListener{ e ->
                Log.e("document error", e.toString())
            }
    }

    private fun redirectUser() {
        val auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser != null) {
            super.onStart()
            val database = FirebaseFirestore.getInstance()
            val userRef = database.collection("users").document(currentUser.uid)
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
        }
    }
}
