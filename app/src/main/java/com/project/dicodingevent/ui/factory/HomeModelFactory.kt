package com.project.dicodingevent.ui.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.dicodingevent.data.EventRepository
import com.project.dicodingevent.di.Injection
import com.project.dicodingevent.ui.model.HomeViewModel

class HomeModelFactory private constructor(
    private val eventRepository: EventRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                HomeViewModel(eventRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

    companion object {
        @Volatile
        private var instance: HomeModelFactory? = null

        fun getInstance(context: Context): HomeModelFactory =
            instance ?: synchronized(this) {
                instance ?: HomeModelFactory(
                    Injection.provideRepository(context)
                ).also { instance = it }
            }
    }
}