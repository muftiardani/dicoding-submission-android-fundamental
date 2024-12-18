package com.project.dicodingevent.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.dicodingevent.data.EventRepository
import com.project.dicodingevent.data.Result
import com.project.dicodingevent.data.local.entity.EventEntity
import com.project.dicodingevent.util.EventWrapper

class UpcomingViewModel(private val eventRepository: EventRepository) : ViewModel() {

    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<EventWrapper<String>>()
    val errorMessage: LiveData<EventWrapper<String>> = _errorMessage

    init {
        fetchUpcomingEvents()
    }

    private fun fetchUpcomingEvents() {
        _isLoading.value = true
        eventRepository.fetchUpcomingEvents().observeForever { result ->
            handleResult(result)
        }
    }

    private fun handleResult(result: Result<List<EventEntity>>) {
        when (result) {
            is Result.Loading -> {
                _isLoading.value = true
            }
            is Result.Success -> {
                _isLoading.value = false
                _uiState.value = UiState(upcomingEvents = result.data)
            }
            is Result.Error -> {
                _isLoading.value = false
                _errorMessage.value = EventWrapper("Error: ${result.error}")
            }
        }
    }

    suspend fun toggleEventFavorite(event: EventEntity, isFavorite: Boolean) {
        eventRepository.setEventFavorite(event, isFavorite)
    }

    data class UiState(
        val upcomingEvents: List<EventEntity> = emptyList()
    )
}