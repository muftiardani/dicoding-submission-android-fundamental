package com.project.dicodingevent.ui.upcoming

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.dicodingevent.data.response.EventResponse
import com.project.dicodingevent.data.response.ListEventsItem
import com.project.dicodingevent.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpcomingViewModel : ViewModel() {
    companion object {
        private const val TAG = "UpcomingViewModel"
        private const val UPCOMING_ID = "1"
    }

    // UI State
    private val _listEvent = MutableLiveData<List<ListEventsItem>>()
    val listEvent: LiveData<List<ListEventsItem>> = _listEvent

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        fetchUpcomingEvents()
    }

    private fun fetchUpcomingEvents() {
        _isLoading.value = true
        _errorMessage.value = null

        ApiConfig.getApiService().getEvents(UPCOMING_ID).enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                handleResponse(response)
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                handleError(t)
            }
        })
    }

    private fun handleResponse(response: Response<EventResponse>) {
        _isLoading.value = false
        if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {
                _listEvent.value = responseBody.listEvents
            } else {
                _errorMessage.value = "Data tidak ditemukan"
            }
        } else {
            handleApiError(response)
        }
    }

    private fun handleApiError(response: Response<EventResponse>) {
        _errorMessage.value = "Gagal memuat data: ${response.message()}"
        Log.e(TAG, "API Error: ${response.message()}")
    }

    private fun handleError(throwable: Throwable) {
        _isLoading.value = false
        _errorMessage.value = "Gagal memuat data: ${throwable.message}"
        Log.e(TAG, "Network Error: ${throwable.message}")
    }
}