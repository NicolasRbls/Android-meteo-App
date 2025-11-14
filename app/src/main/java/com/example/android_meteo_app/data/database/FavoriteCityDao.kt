package com.example.android_meteo_app.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteCityDao {

    @Query("SELECT * FROM favorite_cities ORDER BY name ASC")
    fun getFavoriteCities(): Flow<List<FavoriteCityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(city: FavoriteCityEntity)

    @Delete
    suspend fun removeFavorite(city: FavoriteCityEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_cities WHERE id = :cityId)")
    fun isFavorite(cityId: Int): Flow<Boolean>
}
