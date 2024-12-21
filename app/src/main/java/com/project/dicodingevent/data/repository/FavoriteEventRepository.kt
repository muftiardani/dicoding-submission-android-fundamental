package com.project.dicodingevent.data.repository

import com.project.dicodingevent.data.database.FavoriteEvent
import android.app.Application
import androidx.lifecycle.LiveData
import com.project.dicodingevent.data.database.FavoriteEventDao
import com.project.dicodingevent.data.database.FavoriteEventDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FavoriteEventRepository(application: Application) {
    private val mFavoriteEventDao: FavoriteEventDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = FavoriteEventDatabase.getDatabase(application)
        mFavoriteEventDao = db.favoriteEventDao()
    }

    fun getAllFavoriteEvents(): LiveData<List<FavoriteEvent>> =
        mFavoriteEventDao.getAllFavoriteEvents()

    fun insert(favoriteEvent: FavoriteEvent) {
        executorService.execute { mFavoriteEventDao.insert(favoriteEvent) }
    }

    fun getFavoriteEventById(id: String): LiveData<FavoriteEvent> {
        return mFavoriteEventDao.getFavoriteEventById(id)
    }

    fun delete(favoriteEvent: FavoriteEvent) {
        executorService.execute { mFavoriteEventDao.delete(favoriteEvent) }
    }
}