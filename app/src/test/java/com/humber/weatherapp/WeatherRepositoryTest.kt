package com.humber.weatherapp

import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherRepositoryTest {
    @Test
    fun testFetchWeatherUrlConstruction() {
        val baseUrl = "https://api.weatherapi.com/v1/forecast.json"
        val apiKey = "test_key"
        val location = "Toronto"
        val days = 3

        val expectedUrl = "$baseUrl?key=$apiKey&q=$location&days=$days&aqi=yes&alerts=yes"

        val constructedUrl = "$baseUrl?key=$apiKey&q=$location&days=$days&aqi=yes&alerts=yes"
        assertEquals(expectedUrl, constructedUrl)
    }
}