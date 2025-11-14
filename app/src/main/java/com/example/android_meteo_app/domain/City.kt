package com.example.android_meteo_app.domain

data class City(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val admin1: String? // State or region
)
