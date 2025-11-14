package com.example.android_meteo_app.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeocodingResponse(
    val results: List<CityResult>? = null
)

@Serializable
data class CityResult(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String? = null,
    @SerialName("admin1")
    val admin1: String? = null // State or region
)
