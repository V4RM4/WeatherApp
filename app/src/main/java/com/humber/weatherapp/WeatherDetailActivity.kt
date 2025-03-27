package com.humber.weatherapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView

class WeatherDetailActivity : ComponentActivity() {

    private lateinit var locationDateTime: TextView
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
    private lateinit var windDirCard: CardView
    private lateinit var precipCard: CardView
    private lateinit var cloudCard: CardView
    private lateinit var windChillCard: CardView
    private lateinit var heatIndexCard: CardView
    private lateinit var dewPointCard: CardView
    private lateinit var gustCard: CardView
    private lateinit var moonPhaseCard: CardView
    private lateinit var bottomNav: BottomNavigationView

    private lateinit var apiKey: String
    private lateinit var baseUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)  // Reusing the same layout as WeatherActivity

        bottomNav = findViewById(R.id.bottom_navigation)

        // Set the initial selected item
        bottomNav.selectedItemId = R.id.nav_locations

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

        // Initialize API values
        apiKey = getString(R.string.weather_api_key)
        baseUrl = getString(R.string.base_url)

        // Initialize UI components
        locationDateTime = findViewById(R.id.localDateTime)
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
        windDirCard = findViewById(R.id.windDirCard)
        precipCard = findViewById(R.id.precipCard)
        cloudCard = findViewById(R.id.cloudCard)
        windChillCard = findViewById(R.id.windChillCard)
        heatIndexCard = findViewById(R.id.heatIndexCard)
        dewPointCard = findViewById(R.id.dewPointCard)
        gustCard = findViewById(R.id.gustCard)
        moonPhaseCard = findViewById(R.id.moonPhaseCard)

        // Get city from intent
        val city = intent.getStringExtra("CITY") ?: "London"

        // Fetch weather data for the city
        fetchWeatherData(city)
    }

    private fun fetchWeatherData(city: String) {
        WeatherRepository.fetchWeather(baseUrl, apiKey, this, city, 1) { weatherResponse ->
            if (weatherResponse != null) {
                updateUI(weatherResponse)
            } else {
                Toast.makeText(this, "Error fetching weather data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(weatherData: WeatherResponse) {
        runOnUiThread {
            locationDateTime.text = weatherData.location.localtime
            locationName.text = "${weatherData.location.name}, ${weatherData.location.region}, ${weatherData.location.country}"

            // Temperature - with toggle functionality
            val tempTextView = tempCard.findViewById<TextView>(R.id.tempText)
            var isTempCelsius = true
            val tempC = weatherData.current.temp_c
            val tempF = weatherData.current.temp_f
            tempTextView.text = "🌡 Temperature: $tempC°C"

            tempCard.setOnClickListener {
                if (isTempCelsius) {
                    tempTextView.text = "🌡 Temperature: $tempF°F"
                } else {
                    tempTextView.text = "🌡 Temperature: $tempC°C"
                }
                isTempCelsius = !isTempCelsius
            }

            // Feels Like - with toggle functionality
            val feelsLikeTextView = feelsLikeCard.findViewById<TextView>(R.id.feelsLikeText)
            var isCelsius = true
            val feelsLikeCelsius = weatherData.current.feelslike_c
            val feelsLikeFahrenheit = weatherData.current.feelslike_f
            feelsLikeTextView.text = "🤔 Feels Like: $feelsLikeCelsius°C"

            feelsLikeCard.setOnClickListener {
                if (isCelsius) {
                    feelsLikeTextView.text = "🤔 Feels Like: $feelsLikeFahrenheit°F"
                } else {
                    feelsLikeTextView.text = "🤔 Feels Like: $feelsLikeCelsius°C"
                }
                isCelsius = !isCelsius
            }

            // Wind Speed - with toggle functionality
            val windSpeedTextView = windSpeedCard.findViewById<TextView>(R.id.windSpeedText)
            var isKph = true
            val windKph = weatherData.current.wind_kph
            val windMph = weatherData.current.wind_mph
            windSpeedTextView.text = "💨 Wind Speed: $windKph km/h"

            windSpeedCard.setOnClickListener {
                if (isKph) {
                    windSpeedTextView.text = "💨 Wind Speed: $windMph mph"
                } else {
                    windSpeedTextView.text = "💨 Wind Speed: $windKph km/h"
                }
                isKph = !isKph
            }

            // Weather Description
            val description = weatherData.current.condition.text
            descriptionCard.findViewById<TextView>(R.id.descriptionText).text = "🌥 Description: $description"

            // Humidity
            val humidity = weatherData.current.humidity
            humidityCard.findViewById<TextView>(R.id.humidityText).text = "💧 Humidity: $humidity%"

            // Pressure - with toggle functionality
            val pressureTextView = pressureCard.findViewById<TextView>(R.id.pressureText)
            var isMb = true
            val pressureMb = weatherData.current.pressure_mb
            val pressureIn = weatherData.current.pressure_in
            pressureTextView.text = "⚙️ Pressure: $pressureMb hPa"

            pressureCard.setOnClickListener {
                if (isMb) {
                    pressureTextView.text = "⚙️ Pressure: $pressureIn inHg"
                } else {
                    pressureTextView.text = "⚙️ Pressure: $pressureMb hPa"
                }
                isMb = !isMb
            }

            // Visibility - with toggle functionality
            val visibilityTextView = visibilityCard.findViewById<TextView>(R.id.visibilityText)
            var isKm = true
            val visKm = weatherData.current.vis_km
            val visMiles = weatherData.current.vis_miles
            visibilityTextView.text = "👀 Visibility: $visKm km"

            visibilityCard.setOnClickListener {
                if (isKm) {
                    visibilityTextView.text = "👀 Visibility: $visMiles miles"
                } else {
                    visibilityTextView.text = "👀 Visibility: $visKm km"
                }
                isKm = !isKm
            }

            // AQI (Air Quality Index)
            val aqi = weatherData.current.air_quality.us_epa_index
            aqiCard.findViewById<TextView>(R.id.aqiText).text = "🌫 AQI: $aqi"

            // UV Index
            val uvIndex = weatherData.current.uv
            uvIndexCard.findViewById<TextView>(R.id.uvText).text = "☀️ UV Index: $uvIndex"

            // Sunrise Time
            val sunrise = weatherData.forecast.forecastday[0].astro.sunrise
            sunriseTimeCard.findViewById<TextView>(R.id.sunriseText).text = "🌅 Sunrise: $sunrise"

            // Sunset Time
            val sunset = weatherData.forecast.forecastday[0].astro.sunset
            sunsetTimeCard.findViewById<TextView>(R.id.sunsetText).text = "🌇 Sunset: $sunset"

            // Wind Direction
            val windDir = weatherData.current.wind_dir
            val windDegree = weatherData.current.wind_degree
            windDirCard.findViewById<TextView>(R.id.windDirText).text = "🧭 Wind Direction: $windDir ($windDegree°)"

            // Precipitation - with toggle functionality
            val precipTextView = precipCard.findViewById<TextView>(R.id.precipText)
            var isPrecipMm = true
            val precipMm = weatherData.current.precip_mm
            val precipIn = weatherData.current.precip_in
            precipTextView.text = "🌧 Precipitation: $precipMm mm"

            precipCard.setOnClickListener {
                if (isPrecipMm) {
                    precipTextView.text = "🌧 Precipitation: $precipIn in"
                } else {
                    precipTextView.text = "🌧 Precipitation: $precipMm mm"
                }
                isPrecipMm = !isPrecipMm
            }

            // Cloud Cover
            val cloud = weatherData.current.cloud
            cloudCard.findViewById<TextView>(R.id.cloudText).text = "☁️ Cloud Cover: $cloud%"

            // Wind Chill - with toggle functionality
            val windChillTextView = windChillCard.findViewById<TextView>(R.id.windChillText)
            var isWindChillCelsius = true
            val windChillC = weatherData.current.windchill_c
            val windChillF = weatherData.current.windchill_f
            windChillTextView.text = "❄️ Wind Chill: $windChillC°C"

            windChillCard.setOnClickListener {
                if (isWindChillCelsius) {
                    windChillTextView.text = "❄️ Wind Chill: $windChillF°F"
                } else {
                    windChillTextView.text = "❄️ Wind Chill: $windChillC°C"
                }
                isWindChillCelsius = !isWindChillCelsius
            }

            // Heat Index - with toggle functionality
            val heatIndexTextView = heatIndexCard.findViewById<TextView>(R.id.heatIndexText)
            var isHeatIndexCelsius = true
            val heatIndexC = weatherData.current.heatindex_c
            val heatIndexF = weatherData.current.heatindex_f
            heatIndexTextView.text = "🔥 Heat Index: $heatIndexC°C"

            heatIndexCard.setOnClickListener {
                if (isHeatIndexCelsius) {
                    heatIndexTextView.text = "🔥 Heat Index: $heatIndexF°F"
                } else {
                    heatIndexTextView.text = "🔥 Heat Index: $heatIndexC°C"
                }
                isHeatIndexCelsius = !isHeatIndexCelsius
            }

            // Dew Point - with toggle functionality
            val dewPointTextView = dewPointCard.findViewById<TextView>(R.id.dewPointText)
            var isDewPointCelsius = true
            val dewPointC = weatherData.current.dewpoint_c
            val dewPointF = weatherData.current.dewpoint_f
            dewPointTextView.text = "💦 Dew Point: $dewPointC°C"

            dewPointCard.setOnClickListener {
                if (isDewPointCelsius) {
                    dewPointTextView.text = "💦 Dew Point: $dewPointF°F"
                } else {
                    dewPointTextView.text = "💦 Dew Point: $dewPointC°C"
                }
                isDewPointCelsius = !isDewPointCelsius
            }

            // Wind Gust - with toggle functionality
            val gustTextView = gustCard.findViewById<TextView>(R.id.gustText)
            var isGustKph = true
            val gustKph = weatherData.current.gust_kph
            val gustMph = weatherData.current.gust_mph
            gustTextView.text = "🌬 Wind Gust: $gustKph km/h"

            gustCard.setOnClickListener {
                if (isGustKph) {
                    gustTextView.text = "🌬 Wind Gust: $gustMph mph"
                } else {
                    gustTextView.text = "🌬 Wind Gust: $gustKph km/h"
                }
                isGustKph = !isGustKph
            }

            // Moon Phase
            val moonPhase = weatherData.forecast.forecastday[0].astro.moon_phase
            val moonIllumination = weatherData.forecast.forecastday[0].astro.moon_illumination
            moonPhaseCard.findViewById<TextView>(R.id.moonPhaseText).text =
                "🌙 Moon Phase: $moonPhase ($moonIllumination% illuminated)"
        }
    }
}
