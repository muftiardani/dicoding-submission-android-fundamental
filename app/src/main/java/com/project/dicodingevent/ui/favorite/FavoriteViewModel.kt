package com.project.dicodingevent.ui.favorite

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.project.dicodingevent.data.database.FavoriteEvent
import com.project.dicodingevent.data.repository.FavoriteEventRepository

class FavoriteViewModel(application: Application) : ViewModel() {
    private val mFavoriteEventRepository: FavoriteEventRepository = FavoriteEventRepository(application)

    fun getFavoriteEvents(): LiveData<List<FavoriteEvent>> {
        return mFavoriteEventRepository.getAllFavoriteEvents()
    }
}