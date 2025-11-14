package com.example.android_meteo_app.data

import com.example.android_meteo_app.data.database.FavoriteCityDao
import com.example.android_meteo_app.data.database.WeatherCacheDao
import com.example.android_meteo_app.data.database.WeatherCacheEntity
import com.example.android_meteo_app.data.dto.WeatherForecastResponse
import com.example.android_meteo_app.domain.City
import com.example.android_meteo_app.domain.WeatherInfo
import com.example.android_meteo_app.domain.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val geocodingApiService: GeocodingApiService,
    private val forecastApiService: ForecastApiService,
    private val favoriteCityDao: FavoriteCityDao,
    private val weatherCacheDao: WeatherCacheDao,
    private val json: Json
) : WeatherRepository {

    override suspend fun searchCity(name: String): Result<List<City>> {
        return try {
            val response = geocodingApiService.searchCity(name)
            val cities = response.results?.map { it.toDomain() } ?: emptyList()
            Result.success(cities)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getWeather(city: City): Result<WeatherInfo> {
        val cacheKey = "${city.latitude}_${city.longitude}"
        val cacheDuration = 3600 * 1000 // 1 hour in milliseconds

        // Try to fetch from cache
        val cachedEntity = weatherCacheDao.get(cacheKey)
        if (cachedEntity != null && (System.currentTimeMillis() - cachedEntity.timestamp) < cacheDuration) {
            return try {
                val weatherResponse = json.decodeFromString<WeatherForecastResponse>(cachedEntity.jsonResponse)
                Result.success(weatherResponse.toDomain(city))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        // If cache is invalid or not present, fetch from network
        return try {
            val weatherResponse = forecastApiService.getWeatherForecast(city.latitude, city.longitude)
            // Save to cache
            val jsonResponse = json.encodeToString(weatherResponse)
            weatherCacheDao.insert(WeatherCacheEntity(cacheKey, jsonResponse, System.currentTimeMillis()))
            Result.success(weatherResponse.toDomain(city))
        } catch (e: Exception) {
            // If network fails, try to use expired cache as a last resort
            if (cachedEntity != null) {
                try {
                    val weatherResponse = json.decodeFromString<WeatherForecastResponse>(cachedEntity.jsonResponse)
                    Result.success(weatherResponse.toDomain(city))
                } catch (e: Exception) {
                    Result.failure(e)
                }
            } else {
                Result.failure(e)
            }
        }
    }

    override fun getFavoriteCities(): Flow<List<City>> {
        return favoriteCityDao.getFavoriteCities().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addFavoriteCity(city: City) {
        favoriteCityDao.addFavorite(city.toEntity())
    }

    override suspend fun removeFavoriteCity(city: City) {
        favoriteCityDao.removeFavorite(city.toEntity())
    }

    override fun isFavorite(cityId: Int): Flow<Boolean> {
        return favoriteCityDao.isFavorite(cityId)
    }
}
