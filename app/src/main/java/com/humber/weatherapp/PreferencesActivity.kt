package com.humber.weatherapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.humber.weatherapp.databinding.ActivityPreferencesBinding

class PreferencesActivity : AppCompatActivity() {

    private lateinit var prefBinding: ActivityPreferencesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefBinding = ActivityPreferencesBinding.inflate(layoutInflater)
        setContentView(prefBinding.root)

        val auth = FirebaseAuth.getInstance()

        val database = FirebaseFirestore.getInstance()

        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            val usersCollection = database.collection("users")
            val userDocument = usersCollection.document(userId)
            userDocument.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val userData = document.data
                        val locationField = userData?.get("location")

                        when (locationField) {
                            null -> {
                                // Handle null case
                                prefBinding.cityEditText.setText("")
                            }
                            is List<*> -> {
                                // Handle array/list case
                                val locationList = locationField as List<*>
                                if (locationList.isNotEmpty()) {
                                    // Display the first location or join all locations
                                    //Show just the first location
                                    prefBinding.cityEditText.setText(locationList[0].toString())
                                } else {
                                    prefBinding.cityEditText.setText("")
                                }
                            }
                            else -> {
                                // Handle single string case (just in case)
                                prefBinding.cityEditText.setText(locationField.toString())
                            }
                        }

                        val checkboxes = userData?.get("preferences")
                        Log.d("PreferencesDebug", "Preferences data: $checkboxes (${checkboxes?.javaClass?.name})")

                        when (checkboxes) {
                            is Map<*, *> -> {
                                val preferencesMap = checkboxes as Map<*, *>

                                // Set checkbox states from the map
                                preferencesMap["feels_like"]?.let {
                                    prefBinding.cboxFeelsLike.isChecked = it as Boolean
                                }
                                preferencesMap["wind_speed"]?.let {
                                    prefBinding.cboxWindSpeed.isChecked = it as Boolean
                                }
                                preferencesMap["aqi"]?.let {
                                    prefBinding.cboxAQI.isChecked = it as Boolean
                                }
                                preferencesMap["pressure"]?.let {
                                    prefBinding.cboxPressure.isChecked = it as Boolean
                                }
                                preferencesMap["precipitation"]?.let {
                                    prefBinding.cboxPrecip.isChecked = it as Boolean
                                }
                                preferencesMap["gust"]?.let {
                                    prefBinding.cboxGust.isChecked = it as Boolean
                                }
                                preferencesMap["uv"]?.let {
                                    prefBinding.cboxUV.isChecked = it as Boolean
                                }
                                preferencesMap["humidity"]?.let {
                                    prefBinding.cboxHumid.isChecked = it as Boolean
                                }
                                preferencesMap["wind_direction"]?.let {
                                    prefBinding.cboxWindDir.isChecked = it as Boolean
                                }
                                preferencesMap["sunrise"]?.let {
                                    prefBinding.cboxSunrise.isChecked = it as Boolean
                                }
                                preferencesMap["sunset"]?.let {
                                    prefBinding.cboxSunset.isChecked = it as Boolean
                                }
                                preferencesMap["visibility"]?.let {
                                    prefBinding.cboxVis.isChecked = it as Boolean
                                }
                                preferencesMap["dew_point"]?.let {
                                    prefBinding.cboxDewPoint.isChecked = it as Boolean
                                }
                            }

                            else -> {
                                Log.d("PreferencesDebug", "Preferences in unexpected format")
                            }
                        }

                    }
                }
                .addOnFailureListener { error ->
                    Toast.makeText(this, "Error retrieving document: ${error.message}", Toast.LENGTH_LONG).show()
                }
        }

        prefBinding.btnSearch.setOnClickListener {
            val city = prefBinding.cityEditText.text.toString().trim()
            if (city.isNotEmpty()) {

                getWeather(city) { location ->

                    prefBinding.tvCoordinates.text = "Your location: ${location}"
                    Log.d("Weather", "Received location: $location")
                }
            } else {
                Toast.makeText(this, "Input cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        prefBinding.btnContinue.setOnClickListener {
            val city = prefBinding.tvCoordinates.text
            if (city.isEmpty()) {
                Toast.makeText(this, "Please search a location first!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else {
                val location = prefBinding.cityEditText.text.toString().trim()
                getWeather(location) { loc ->
                    database.collection("users").document(currentUser!!.uid)
                        .set(checkOptionsSelected(loc))
                }
                val homeIntent = Intent(this, HomeActivity::class.java)
                startActivity(homeIntent)
                finish()
            }
        }
    }

    private fun checkOptionsSelected(city: String): HashMap<String, Any> {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val weatherOptions = hashMapOf(
            "user_info" to hashMapOf(
                "user_id" to currentUser?.uid,
                "display_name" to currentUser?.displayName,
                "email" to currentUser?.email,
                "photoUrl" to currentUser?.photoUrl
            ),
            "location" to mutableListOf(city),
            "preferences" to hashMapOf(
                "feels_like" to prefBinding.cboxFeelsLike.isChecked,
                "wind_speed" to prefBinding.cboxWindSpeed.isChecked,
                "aqi" to prefBinding.cboxAQI.isChecked,
                "pressure" to prefBinding.cboxPressure.isChecked,
                "precipitation" to prefBinding.cboxPrecip.isChecked,
                "gust" to prefBinding.cboxGust.isChecked,
                "uv" to prefBinding.cboxUV.isChecked,
                "humidity" to prefBinding.cboxHumid.isChecked,
                "wind_direction" to prefBinding.cboxWindDir.isChecked,
                "sunrise" to prefBinding.cboxSunrise.isChecked,
                "sunset" to prefBinding.cboxSunset.isChecked,
                "visibility" to prefBinding.cboxVis.isChecked,
                "dew_point" to prefBinding.cboxDewPoint.isChecked
            )
        )
        return weatherOptions
    }

    private fun getWeather(city: String, onLocationReceived: (String) -> Unit) {
        val API_KEY = getString(R.string.weather_api_key)
        val BASE_URL = getString(R.string.base_url)
        WeatherRepository.fetchWeather(BASE_URL, API_KEY, this, city, 5) { weatherResponse ->
            weatherResponse?.let {
                val location = "${it.location.name}, ${it.location.region}, ${it.location.country}"
                Toast.makeText(this, location, Toast.LENGTH_LONG).show()
                Log.d("Weather", "Location: ${it.location.name}, Temp: ${it.current.temp_c}°C")
                onLocationReceived(location)
            } ?: Log.e("Weather", "Failed to fetch data")
        }
    }

}