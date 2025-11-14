package com.example.android_meteo_app.domain

import java.time.LocalDateTime

data class WeatherInfo(
    val city: City,
    val currentTemperature: Double,
    val currentCondition: WeatherCondition,
    val hourlyForecast: List<HourlyWeather>,
    val minTemperature: Double,
    val maxTemperature: Double,
    val windSpeed: Double
)

data class HourlyWeather(
    val time: LocalDateTime,
    val temperature: Double,
    val humidity: Int,
    val windSpeed: Double,
    val rain: Double,
    val condition: WeatherCondition
)

enum class WeatherCondition {
    SUNNY,
    CLOUDY,
    RAINY,
    UNKNOWN
}
