package com.example.android_meteo_app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    @PrimaryKey
    val latLon: String, // e.g., "48.85_2.35"
    val jsonResponse: String,
    val timestamp: Long
)
