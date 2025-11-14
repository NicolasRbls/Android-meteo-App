package com.example.android_meteo_app.data

import com.example.android_meteo_app.data.dto.WeatherForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ForecastApiService {
    @GET("v1/forecast")
    suspend fun getWeatherForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("hourly") hourly: String = "temperature_2m,relative_humidity_2m,apparent_temperature,rain,wind_speed_10m",
        @Query("models") models: String = "meteofrance_seamless",
        @Query("timezone") timezone: String = "auto"
    ): WeatherForecastResponse
}
