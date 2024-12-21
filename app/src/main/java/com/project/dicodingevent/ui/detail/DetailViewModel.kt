package com.project.dicodingevent.ui.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.dicodingevent.data.database.FavoriteEvent
import com.project.dicodingevent.data.repository.FavoriteEventRepository
import com.project.dicodingevent.data.response.DetailEventResponse
import com.project.dicodingevent.data.response.Event
import com.project.dicodingevent.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel(eventId: String, application: Application) : ViewModel() {
    companion object {
        private const val TAG = "DetailViewModel"
    }

    // Repository
    private val favoriteEventRepository: FavoriteEventRepository = FavoriteEventRepository(application)

    // LiveData for UI states
    private val _event = MutableLiveData<Event>()
    val event: LiveData<Event> = _event

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Favorite event LiveData
    private val _favoriteEvent = favoriteEventRepository.getFavoriteEventById(eventId)
    val favoriteEvent: LiveData<FavoriteEvent> = _favoriteEvent

    init {
        fetchEventDetail(eventId)
    }

    // Network operations
    private fun fetchEventDetail(eventId: String) {
        _isLoading.value = true
        _errorMessage.value = null

        ApiConfig.getApiService().getDetailEvent(eventId).enqueue(object : Callback<DetailEventResponse> {
            override fun onResponse(
                call: Call<DetailEventResponse>,
                response: Response<DetailEventResponse>
            ) {
                _isLoading.value = false
                handleEventResponse(response)
            }

            override fun onFailure(call: Call<DetailEventResponse>, t: Throwable) {
                handleEventError(t)
            }
        })
    }

    private fun handleEventResponse(response: Response<DetailEventResponse>) {
        if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {
                _event.value = responseBody.event
            } else {
                _errorMessage.value = "Data tidak ditemukan"
            }
        } else {
            _errorMessage.value = "Gagal memuat data: ${response.message()}"
        }
    }

    private fun handleEventError(throwable: Throwable) {
        _isLoading.value = false
        _errorMessage.value = "Gagal memuat data: ${throwable.message}"
        Log.e(TAG, "Network error: ${throwable.message}")
    }

    // Database operations
    fun insert(favoriteEvent: FavoriteEvent) {
        favoriteEventRepository.insert(favoriteEvent)
    }

    fun deleteFavorite(favoriteEvent: FavoriteEvent) {
        favoriteEventRepository.delete(favoriteEvent)
    }
}