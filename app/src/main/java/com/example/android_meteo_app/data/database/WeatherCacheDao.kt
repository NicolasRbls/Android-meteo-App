package com.example.android_meteo_app.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherCache: WeatherCacheEntity)

    @Query("SELECT * FROM weather_cache WHERE latLon = :latLon")
    suspend fun get(latLon: String): WeatherCacheEntity?

    @Query("DELETE FROM weather_cache WHERE latLon = :latLon")
    suspend fun delete(latLon: String)

    @Query("DELETE FROM weather_cache")
    suspend fun clearCache()
}
