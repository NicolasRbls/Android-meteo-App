package com.example.android_meteo_app.domain

import android.location.Location

interface LocationTracker {
    suspend fun getCurrentLocation(): Location?
    suspend fun getCityName(latitude: Double, longitude: Double): String?
}
