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
                    if(document.exists()){
                        val userData = document.data
                        val savedLocation = userData?.get("location").toString()
                        if (savedLocation == "null"){
                            prefBinding.cityEditText.setText("")
                        } else {
                            prefBinding.cityEditText.setText(savedLocation)
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
                getWeather(city)
            } else {
                Toast.makeText(this, "Input cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        prefBinding.btnContinue.setOnClickListener {
            val city = prefBinding.tvCoordinates.text
            if (city.isEmpty()){
                Toast.makeText(this, "Please search a location first!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else {
                database.collection("users").document(currentUser!!.uid).set(checkOptionsSelected(city.toString()))
                val homeIntent = Intent(this, HomeActivity::class.java)
                startActivity(homeIntent)
                finish()
            }
        }
    }

    private fun checkOptionsSelected(city: String): HashMap<String, Any> {
        val weatherOptions = hashMapOf(
            "location" to mutableListOf(city),
            "feels_like" to prefBinding.cboxFeelsLike.isChecked,
            "wind_speed" to prefBinding.cboxWindSpeed.isChecked,
            "aqi" to prefBinding.cboxAQI.isChecked,
            "pressure" to prefBinding.cboxAQI.isChecked,
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
        return weatherOptions
    }

    private fun getWeather(city: String) {
        val API_KEY = getString(R.string.weather_api_key)
        val BASE_URL = getString(R.string.base_url)
        WeatherRepository.fetchWeather(BASE_URL, API_KEY, this, city, 5) { weatherResponse ->
            weatherResponse?.let {
                prefBinding.tvCoordinates.text = "Your location: ${it.location.name}, ${it.location.region}, ${it.location.country}"
                Log.d("Weather", "Location: ${it.location.name}, Temp: ${it.current.temp_c}°C")
            } ?: Log.e("Weather", "Failed to fetch data")

        }
        return
    }
}