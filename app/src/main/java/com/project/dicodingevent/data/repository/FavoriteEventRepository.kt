package com.project.dicodingevent.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.project.dicodingevent.data.database.FavoriteEvent
import com.project.dicodingevent.data.database.FavoriteEventDao
import com.project.dicodingevent.data.database.FavoriteEventDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FavoriteEventRepository(application: Application) {
    // Database and Executor
    private val favoriteEventDao: FavoriteEventDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val database = FavoriteEventDatabase.getDatabase(application)
        favoriteEventDao = database.favoriteEventDao()
    }

    // Database Operations
    fun getAllFavoriteEvents(): LiveData<List<FavoriteEvent>> =
        favoriteEventDao.getAllFavoriteEvents()

    fun getFavoriteEventById(id: String): LiveData<FavoriteEvent> =
        favoriteEventDao.getFavoriteEventById(id)

    fun insert(favoriteEvent: FavoriteEvent) {
        executeInBackground { favoriteEventDao.insert(favoriteEvent) }
    }

    fun delete(favoriteEvent: FavoriteEvent) {
        executeInBackground { favoriteEventDao.delete(favoriteEvent) }
    }

    // Helper Methods
    private fun executeInBackground(operation: () -> Unit) {
        executorService.execute { operation() }
    }

    companion object {
        private const val TAG = "FavoriteEventRepository"
    }
}