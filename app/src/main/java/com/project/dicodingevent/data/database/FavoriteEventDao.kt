package com.project.dicodingevent.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteEventDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(favoriteEvent: FavoriteEvent)

    @Query("SELECT * FROM favorite_event WHERE id = :id")
    fun getFavoriteEventById(id: String): LiveData<FavoriteEvent>

    @Delete
    fun delete(favoriteEvent: FavoriteEvent)

    @Query("SELECT * FROM favorite_event")
    fun getAllFavoriteEvents(): LiveData<List<FavoriteEvent>>
}