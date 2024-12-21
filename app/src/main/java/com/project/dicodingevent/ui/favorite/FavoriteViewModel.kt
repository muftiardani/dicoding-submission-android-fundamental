package com.project.dicodingevent.ui.favorite

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.project.dicodingevent.data.database.FavoriteEvent
import com.project.dicodingevent.data.repository.FavoriteEventRepository

class FavoriteViewModel(application: Application) : ViewModel() {
    // Repository
    private val favoriteEventRepository = FavoriteEventRepository(application)

    // Public Methods
    fun getFavoriteEvents(): LiveData<List<FavoriteEvent>> =
        favoriteEventRepository.getAllFavoriteEvents()
}