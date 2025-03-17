package com.humber.weatherapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.humber.weatherapp.databinding.ActivityWeatherBinding

class WeatherActivity : AppCompatActivity() {

    private lateinit var weatherBinding: ActivityWeatherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weatherBinding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(weatherBinding.root)
    }
}