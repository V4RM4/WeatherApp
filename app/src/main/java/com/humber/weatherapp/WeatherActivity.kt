package com.humber.weatherapp

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WeatherActivity : ComponentActivity() {

    private lateinit var locationName: TextView
    private lateinit var tempCard: CardView
    private lateinit var feelsLikeCard: CardView
    private lateinit var windSpeedCard: CardView
    private lateinit var descriptionCard: CardView
    private lateinit var humidityCard: CardView
    private lateinit var pressureCard: CardView
    private lateinit var visibilityCard: CardView
    private lateinit var aqiCard: CardView
    private lateinit var uvIndexCard: CardView
    private lateinit var sunriseTimeCard: CardView
    private lateinit var sunsetTimeCard: CardView

    // Don't initialize these with values that require context
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var apiKey: String
    private lateinit var baseUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize API values
        apiKey = getString(R.string.weather_api_key)
        baseUrl = getString(R.string.base_url)

        // Initialize UI components
        locationName = findViewById(R.id.locationName)
        tempCard = findViewById(R.id.tempCard)
        feelsLikeCard = findViewById(R.id.feelsLikeCard)
        windSpeedCard = findViewById(R.id.windSpeedCard)
        descriptionCard = findViewById(R.id.descriptionCard)
        humidityCard = findViewById(R.id.humidityCard)
        pressureCard = findViewById(R.id.pressureCard)
        visibilityCard = findViewById(R.id.visibilityCard)
        aqiCard = findViewById(R.id.aqiCard)
        uvIndexCard = findViewById(R.id.uvCard)
        sunriseTimeCard = findViewById(R.id.sunriseCard)
        sunsetTimeCard = findViewById(R.id.sunsetCard)

        loadUserPreferences()
    }

    private fun loadUserPreferences() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Get location as a list and take the first item
                    val locationList = document.get("location") as? List<String>
                    val city = locationList?.firstOrNull() ?: "Unknown"

                    // Get preferences map
                    val preferences = document.get("preferences") as? Map<String, Boolean> ?: emptyMap()

                    fetchWeatherData(city, preferences)
                } else {
                    Toast.makeText(this, "No preferences found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load preferences", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchWeatherData(city: String, preferences: Map<String, Boolean>) {
        WeatherRepository.fetchWeather(baseUrl, apiKey, this, city, 1) { weatherResponse ->
            if (weatherResponse != null) {
                updateUI(preferences, weatherResponse, city)
            } else {
                Toast.makeText(this, "Error fetching weather data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(preferences: Map<String, Boolean>, weatherData: WeatherResponse, city: String) {
        runOnUiThread {
            locationName.text = weatherData.location.name

            // Always show temperature
            val temp = weatherData.current.temp_c
            tempCard.findViewById<TextView>(R.id.tempText).text = "🌡 Temperature: $temp°C"

            // Feels Like
            if (preferences["feels_like"] == true) {
                val feelsLike = weatherData.current.feelslike_c
                feelsLikeCard.findViewById<TextView>(R.id.feelsLikeText).text = "🤔 Feels Like: $feelsLike°C"
            } else {
                feelsLikeCard.visibility = CardView.GONE
            }

            // Wind Speed
            if (preferences["wind_speed"] == true) {
                val windSpeed = weatherData.current.wind_kph
                windSpeedCard.findViewById<TextView>(R.id.windSpeedText).text = "💨 Wind Speed: $windSpeed km/h"
            } else {
                windSpeedCard.visibility = CardView.GONE
            }

            // Weather Description - always show
            val description = weatherData.current.condition.text
            descriptionCard.findViewById<TextView>(R.id.descriptionText).text = "🌥 Description: $description"

            // Humidity
            if (preferences["humidity"] == true) {
                val humidity = weatherData.current.humidity
                humidityCard.findViewById<TextView>(R.id.humidityText).text = "💧 Humidity: $humidity%"
            } else {
                humidityCard.visibility = CardView.GONE
            }

            // Pressure
            if (preferences["pressure"] == true) {
                val pressure = weatherData.current.pressure_mb
                pressureCard.findViewById<TextView>(R.id.pressureText).text = "⚙️ Pressure: $pressure hPa"
            } else {
                pressureCard.visibility = CardView.GONE
            }

            // Visibility
            if (preferences["visibility"] == true) {
                val visibility = weatherData.current.vis_km
                visibilityCard.findViewById<TextView>(R.id.visibilityText).text = "👀 Visibility: $visibility km"
            } else {
                visibilityCard.visibility = CardView.GONE
            }

            // AQI (Air Quality Index)
            if (preferences["aqi"] == true) {
                val aqi = weatherData.current.air_quality.us_epa_index
                aqiCard.findViewById<TextView>(R.id.aqiText).text = "🌫 AQI: $aqi"
            } else {
                aqiCard.visibility = CardView.GONE
            }

            // UV Index
            if (preferences["uv"] == true) {
                val uvIndex = weatherData.current.uv
                uvIndexCard.findViewById<TextView>(R.id.uvText).text = "☀️ UV Index: $uvIndex"
            } else {
                uvIndexCard.visibility = CardView.GONE
            }

            // Sunrise Time
            if (preferences["sunrise"] == true) {
                val sunrise = weatherData.forecast.forecastday[0].astro.sunrise
                sunriseTimeCard.findViewById<TextView>(R.id.sunriseText).text = "🌅 Sunrise: $sunrise"
            } else {
                sunriseTimeCard.visibility = CardView.GONE
            }

            // Sunset Time
            if (preferences["sunset"] == true) {
                val sunset = weatherData.forecast.forecastday[0].astro.sunset
                sunsetTimeCard.findViewById<TextView>(R.id.sunsetText).text = "🌇 Sunset: $sunset"
            } else {
                sunsetTimeCard.visibility = CardView.GONE
            }
        }
    }
}
