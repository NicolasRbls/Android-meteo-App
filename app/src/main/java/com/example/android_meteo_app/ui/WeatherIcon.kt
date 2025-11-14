package com.example.android_meteo_app.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.android_meteo_app.domain.WeatherCondition

fun getWeatherIcon(condition: WeatherCondition): ImageVector {
    return when (condition) {
        WeatherCondition.SUNNY -> Icons.Default.WbSunny
        WeatherCondition.CLOUDY -> Icons.Default.CloudQueue
        WeatherCondition.RAINY -> Icons.Default.Grain
        WeatherCondition.UNKNOWN -> Icons.Default.Cloud
    }
}
