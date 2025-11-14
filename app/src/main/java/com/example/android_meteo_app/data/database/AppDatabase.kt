package com.example.android_meteo_app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FavoriteCityEntity::class, WeatherCacheEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteCityDao(): FavoriteCityDao
    abstract fun weatherCacheDao(): WeatherCacheDao

    companion object {
        const val DATABASE_NAME = "meteo_app_database"
    }
}
