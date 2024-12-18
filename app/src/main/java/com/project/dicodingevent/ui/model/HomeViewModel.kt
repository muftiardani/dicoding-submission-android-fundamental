package com.project.dicodingevent.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.dicodingevent.data.EventRepository
import com.project.dicodingevent.data.Result
import com.project.dicodingevent.data.local.entity.EventEntity
import com.project.dicodingevent.util.EventWrapper

class HomeViewModel(private val eventRepository: EventRepository) : ViewModel() {

    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<EventWrapper<String>>()
    val errorMessage: LiveData<EventWrapper<String>> = _errorMessage

    init {
        fetchEvents()
    }

    private fun fetchEvents() {
        fetchEventUpcoming()
        fetchEventFinished()
    }

    private fun fetchEventUpcoming() {
        _isLoading.value = true
        eventRepository.fetchUpcomingEvents().observeForever { result ->
            handleEventResult(result) { events ->
                _uiState.value = _uiState.value?.copy(upcomingEvents = events)
                    ?: UiState(upcomingEvents = events)
            }
        }
    }

    private fun fetchEventFinished() {
        _isLoading.value = true
        eventRepository.fetchFinishedEvents().observeForever { result ->
            handleEventResult(result) { events ->
                _uiState.value = _uiState.value?.copy(finishedEvents = events)
                    ?: UiState(finishedEvents = events)
            }
        }
    }

    private fun handleEventResult(
        result: Result<List<EventEntity>>,
        onSuccess: (List<EventEntity>) -> Unit
    ) {
        when (result) {
            is Result.Loading -> _isLoading.value = true
            is Result.Success -> {
                _isLoading.value = false
                onSuccess(result.data)
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
        val upcomingEvents: List<EventEntity> = emptyList(),
        val finishedEvents: List<EventEntity> = emptyList()
    )
}