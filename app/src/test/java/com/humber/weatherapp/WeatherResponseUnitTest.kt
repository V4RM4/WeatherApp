package com.humber.weatherapp

import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test

class WeatherResponseUnitTest {

    private val sampleJson = """
        {
          "location": {
            "name": "Toronto",
            "region": "Ontario",
            "country": "Canada",
            "lat": 43.7,
            "lon": -79.42,
            "tz_id": "America/Toronto",
            "localtime_epoch": 1710000000,
            "localtime": "2025-04-10 10:00"
          },
          "current": {
            "last_updated_epoch": 1710000000,
            "last_updated": "2025-04-10 10:00",
            "temp_c": 15.0,
            "temp_f": 59.0,
            "is_day": 1,
            "condition": {
              "text": "Partly cloudy",
              "icon": "//cdn.weatherapi.com/weather/64x64/day/116.png",
              "code": 1003
            },
            "wind_mph": 5.0,
            "wind_kph": 8.0,
            "wind_degree": 180,
            "wind_dir": "S",
            "pressure_mb": 1012.0,
            "pressure_in": 29.9,
            "precip_mm": 0.0,
            "precip_in": 0.0,
            "humidity": 55,
            "cloud": 25,
            "feelslike_c": 15.0,
            "feelslike_f": 59.0,
            "windchill_c": 15.0,
            "windchill_f": 59.0,
            "heatindex_c": 15.0,
            "heatindex_f": 59.0,
            "dewpoint_c": 7.0,
            "dewpoint_f": 44.6,
            "vis_km": 10.0,
            "vis_miles": 6.0,
            "uv": 5.0,
            "gust_mph": 7.0,
            "gust_kph": 11.0,
            "air_quality": {
              "co": 200.0,
              "no2": 10.0,
              "o3": 50.0,
              "so2": 2.0,
              "pm2_5": 12.0,
              "pm10": 20.0,
              "us_epa_index": 2,
              "gb_defra_index": 1
            }
          },
          "forecast": {
            "forecastday": []
          }
        }
    """.trimIndent()

    @Test
    fun testParsingWeatherResponse() {
        val weatherResponse = Gson().fromJson(sampleJson, WeatherResponse::class.java)

        assertEquals("Toronto", weatherResponse.location.name)
        assertEquals(15.0, weatherResponse.current.temp_c, 0.01)
        assertEquals("Partly cloudy", weatherResponse.current.condition.text)
        assertEquals(2, weatherResponse.current.air_quality.us_epa_index)
    }
}
