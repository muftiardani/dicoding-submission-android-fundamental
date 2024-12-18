package com.project.dicodingevent.di

import android.content.Context
import com.project.dicodingevent.data.EventRepository
import com.project.dicodingevent.data.local.room.EventDatabase
import com.project.dicodingevent.data.remote.retrofit.ApiConfig
import com.project.dicodingevent.util.AppExecutors

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = EventDatabase.getInstance(context)
        val dao = database.eventDao()
        val appExecutors = AppExecutors()
        return EventRepository.getInstance(apiService, dao, appExecutors)
    }
}