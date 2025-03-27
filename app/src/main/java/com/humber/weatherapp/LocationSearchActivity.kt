package com.humber.weatherapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LocationSearchActivity : ComponentActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var locationsContainer: LinearLayout
    private lateinit var sharedPreferences: SharedPreferences
    private val savedLocations = mutableMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_search)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("WeatherLocations", Context.MODE_PRIVATE)

        // Initialize UI components
        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)
        locationsContainer = findViewById(R.id.locationsContainer)

        // Load saved locations
        loadSavedLocations()

        // Set up search button click listener
        searchButton.setOnClickListener {
            val city = searchEditText.text.toString().trim()
            if (city.isNotEmpty()) {
                getWeather(city) { location ->
                    // Save location to SharedPreferences
                    saveLocation(city, location)
                    // Add location as a card
                    addLocationCard(location, city)
                }
            } else {
                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadSavedLocations() {
        val locationsJson = sharedPreferences.getString("locations", null)
        if (locationsJson != null) {
            val type = object : TypeToken<Map<String, String>>() {}.type
            val loadedLocations = Gson().fromJson<Map<String, String>>(locationsJson, type)
            savedLocations.putAll(loadedLocations)

            // Add cards for all saved locations
            savedLocations.forEach { (city, location) ->
                addLocationCard(location, city)
            }
        }
    }

    private fun saveLocation(city: String, location: String) {
        savedLocations[city] = location
        val editor = sharedPreferences.edit()
        val locationsJson = Gson().toJson(savedLocations)
        editor.putString("locations", locationsJson)
        editor.apply()
    }

    private fun deleteLocation(city: String) {
        savedLocations.remove(city)
        val editor = sharedPreferences.edit()
        val locationsJson = Gson().toJson(savedLocations)
        editor.putString("locations", locationsJson)
        editor.apply()
    }

    private fun addLocationCard(location: String, city: String) {
        runOnUiThread {
            // Create a new card view
            val cardView = CardView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16, 16, 16, 16)
                }
                radius = 12f
                elevation = 8f
                setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
                tag = city // Tag the card with the city name for easy identification
            }

            // Create a linear layout for the card content
            val cardContent = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.VERTICAL
                setPadding(16, 16, 16, 16)
                setOnClickListener {
                    // Launch WeatherDetailActivity with the city as an extra
                    val intent = Intent(this@LocationSearchActivity, WeatherDetailActivity::class.java)
                    intent.putExtra("CITY", city)
                    startActivity(intent)
                }
            }

            // Create a text view for the location
            val locationTextView = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                text = location
                textSize = 22f
                setTextColor(ContextCompat.getColor(context, android.R.color.black))
                setPadding(16, 16, 16, 8)
            }

            // Create a horizontal layout for buttons
            val buttonLayout = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.HORIZONTAL
                weightSum = 2f
            }

            // Create a delete button
            val deleteButton = Button(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    weight = 0.2f
                }
                text = "❌"
                setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
                setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
                minWidth = 0
                minimumWidth = 0
                setPadding(0, 0, 0, 0)

                setOnClickListener {
                    // Remove the card from the container
                    locationsContainer.removeView(cardView)
                    // Delete from SharedPreferences
                    deleteLocation(city)
                    Toast.makeText(this@LocationSearchActivity, "Location deleted", Toast.LENGTH_SHORT).show()
                }
            }

            // Add buttons to the button layout
            buttonLayout.addView(deleteButton)

            // Add views to the card
            cardContent.addView(locationTextView)
            cardContent.addView(buttonLayout)
            cardView.addView(cardContent)

            // Remove any existing card with the same city (to avoid duplicates)
            for (i in 0 until locationsContainer.childCount) {
                val child = locationsContainer.getChildAt(i)
                if (child is CardView && child.tag == city) {
                    locationsContainer.removeViewAt(i)
                    break
                }
            }

            // Add the card to the container
            locationsContainer.addView(cardView)
        }
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
            } ?: run {
                Log.e("Weather", "Failed to fetch data")
                Toast.makeText(this, "Failed to fetch weather data for $city", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
