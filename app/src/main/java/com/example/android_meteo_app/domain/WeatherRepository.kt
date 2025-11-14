package com.example.android_meteo_app.domain

import com.example.android_meteo_app.data.database.FavoriteCityEntity
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    suspend fun searchCity(name: String): Result<List<City>>

    suspend fun getWeather(city: City): Result<WeatherInfo>

    fun getFavoriteCities(): Flow<List<City>>

    suspend fun addFavoriteCity(city: City)

    suspend fun removeFavoriteCity(city: City)

    fun isFavorite(cityId: Int): Flow<Boolean>
}
