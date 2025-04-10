package com.humber.weatherapp

import org.junit.Assert.*
import org.junit.Test

class TempUtilsTest {

    @Test
    fun testToggleFromCelsiusToFahrenheit() {
        val result = TempUtils.toggleTemp("🌡 Temperature: 20.0°C", 20.0, 68.0)
        assertEquals("🌡 Temperature: 68.0°F", result)
    }

    @Test
    fun testToggleFromFahrenheitToCelsius() {
        val result = TempUtils.toggleTemp("🌡 Temperature: 68.0°F", 20.0, 68.0)
        assertEquals("🌡 Temperature: 20.0°C", result)
    }
}

object TempUtils {
    fun toggleTemp(current: String, celsius: Double, fahrenheit: Double): String {
        return if (current.contains("°C")) {
            "🌡 Temperature: $fahrenheit°F"
        } else {
            "🌡 Temperature: $celsius°C"
        }
    }
}