package com.example.android_meteo_app.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherForecastResponse(
    val latitude: Double,
    val longitude: Double,
    @SerialName("hourly_units")
    val hourlyUnits: HourlyUnits,
    val hourly: HourlyData
)

@Serializable
data class HourlyUnits(
    @SerialName("temperature_2m")
    val temperature: String,
    @SerialName("relative_humidity_2m")
    val humidity: String,
    @SerialName("wind_speed_10m")
    val windSpeed: String,
    @SerialName("rain")
    val rain: String
)

@Serializable
data class HourlyData(
    val time: List<String>,
    @SerialName("temperature_2m")
    val temperature: List<Double?>,
    @SerialName("relative_humidity_2m")
    val humidity: List<Int?>,
    @SerialName("apparent_temperature")
    val apparentTemperature: List<Double?>,
    val rain: List<Double?>,
    @SerialName("wind_speed_10m")
    val windSpeed: List<Double?>
)
