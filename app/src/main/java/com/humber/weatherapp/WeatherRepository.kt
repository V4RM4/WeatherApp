package com.humber.weatherapp

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson

object WeatherRepository {

    fun fetchWeather(
        BASE_URL: String,
        API_KEY: String,
        context: Context,
        location: String,
        days: Int,
        callback: (WeatherResponse?) -> Unit,
    ) {
        val url = "$BASE_URL?key=$API_KEY&q=$location&days=$days&aqi=yes&alerts=yes"

        val queue = Volley.newRequestQueue(context)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val gson = Gson()
                val weatherData = gson.fromJson(response.toString(), WeatherResponse::class.java)
                callback(weatherData)
            },
            { error ->
                Log.e("WeatherAPI", "Error fetching weather data: ${error.message}")
                callback(null)
            }
        )

        queue.add(jsonObjectRequest)
    }
}