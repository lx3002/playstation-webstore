package com.example.webstore.Seller.favourites

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouritesDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFav(favourites: Favourites) : Unit
    @Query("SELECT * FROM favourites")
    fun getFavs() : Flow<List<Favourites>>
    @Update
    suspend fun updateFav(favourites: Favourites) : Unit
    @Delete
    suspend fun deleteFav(favourites: Favourites) : Unit
}