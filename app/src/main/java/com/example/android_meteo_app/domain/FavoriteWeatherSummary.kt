package com.example.android_meteo_app.domain

data class FavoriteWeatherSummary(
    val city: City,
    val weatherInfo: WeatherInfo? // Can be null if weather data isn't cached and network is off
)
