package com.project.dicodingevent.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.dicodingevent.data.EventRepository
import com.project.dicodingevent.data.local.entity.EventEntity

class FavoriteViewModel(private val eventRepository: EventRepository) : ViewModel() {

    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    suspend fun getFavoriteEvents() = eventRepository.getEventFavorite()

    suspend fun toggleEventFavorite(event: EventEntity, isFavorite: Boolean) {
        eventRepository.setEventFavorite(event, isFavorite)
    }

    data class UiState(
        val favoriteEvents: List<EventEntity> = emptyList()
    )
}