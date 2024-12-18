package com.project.dicodingevent.ui.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.dicodingevent.data.EventRepository
import com.project.dicodingevent.di.Injection
import com.project.dicodingevent.ui.model.FinishedViewModel

class FinishedModelFactory private constructor(
    private val eventRepository: EventRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(FinishedViewModel::class.java) ->
                FinishedViewModel(eventRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

    companion object {
        @Volatile
        private var instance: FinishedModelFactory? = null

        fun getInstance(context: Context): FinishedModelFactory =
            instance ?: synchronized(this) {
                instance ?: FinishedModelFactory(
                    Injection.provideRepository(context)
                ).also { instance = it }
            }
    }
}