package com.example.android_meteo_app.data

import com.example.android_meteo_app.data.database.FavoriteCityEntity
import com.example.android_meteo_app.data.dto.CityResult
import com.example.android_meteo_app.data.dto.WeatherForecastResponse
import com.example.android_meteo_app.domain.City
import com.example.android_meteo_app.domain.HourlyWeather
import com.example.android_meteo_app.domain.WeatherCondition
import com.example.android_meteo_app.domain.WeatherInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun CityResult.toDomain(): City {
    return City(
        id = this.id,
        name = this.name,
        latitude = this.latitude,
        longitude = this.longitude,
        country = this.country ?: "",
        admin1 = this.admin1
    )
}

fun FavoriteCityEntity.toDomain(): City {
    return City(
        id = this.id,
        name = this.name,
        latitude = this.latitude,
        longitude = this.longitude,
        country = this.country,
        admin1 = this.admin1
    )
}

fun City.toEntity(): FavoriteCityEntity {
    return FavoriteCityEntity(
        id = this.id,
        name = this.name,
        latitude = this.latitude,
        longitude = this.longitude,
        country = this.country,
        admin1 = this.admin1
    )
}

fun WeatherForecastResponse.toDomain(city: City): WeatherInfo {
    val now = LocalDateTime.now()
    val currentHourIndex = hourly.time.indexOfFirst { LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME) >= now }
        .takeIf { it != -1 } ?: 0

    val hourlyForecast = hourly.time.mapIndexedNotNull { index, timeString ->
        val temp = hourly.temperature.getOrNull(index)
        val humidity = hourly.humidity.getOrNull(index)
        val wind = hourly.windSpeed.getOrNull(index)
        val rain = hourly.rain.getOrNull(index)

        if (temp != null && humidity != null && wind != null && rain != null) {
            HourlyWeather(
                time = LocalDateTime.parse(timeString, DateTimeFormatter.ISO_DATE_TIME),
                temperature = temp,
                humidity = humidity,
                windSpeed = wind,
                rain = rain,
                condition = determineWeatherCondition(temp, rain)
            )
        } else {
            null
        }
    }

    return WeatherInfo(
        city = city,
        currentTemperature = hourly.temperature.getOrNull(currentHourIndex) ?: 0.0,
        currentCondition = determineWeatherCondition(
            hourly.temperature.getOrNull(currentHourIndex) ?: 0.0,
            hourly.rain.getOrNull(currentHourIndex) ?: 0.0
        ),
        hourlyForecast = hourlyForecast,
        minTemperature = hourly.temperature.mapNotNull { it }.minOrNull() ?: 0.0,
        maxTemperature = hourly.temperature.mapNotNull { it }.maxOrNull() ?: 0.0,
        windSpeed = hourly.windSpeed.getOrNull(currentHourIndex) ?: 0.0
    )
}

private fun determineWeatherCondition(temperature: Double, rain: Double): WeatherCondition {
    return when {
        rain > 0.1 -> WeatherCondition.RAINY
        temperature > 15 -> WeatherCondition.SUNNY // Simplified logic
        else -> WeatherCondition.CLOUDY
    }
}
