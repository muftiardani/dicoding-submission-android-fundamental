package com.project.dicodingevent.ui.finished

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

class FinishedViewModel : ViewModel() {
    companion object {
        private const val TAG = "FinishedViewModel"
        private const val FINISHED_ID = "0"
    }

    // UI State
    private val _listEvent = MutableLiveData<List<ListEventsItem>>()
    val listEvent: LiveData<List<ListEventsItem>> = _listEvent

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        showFinishedEvents()
    }

    fun showFinishedEvents() {
        fetchEvents(ApiConfig.getApiService().getEvents(FINISHED_ID))
    }

    fun searchEvents(query: String) {
        fetchEvents(ApiConfig.getApiService().searchEvents(query))
    }

    private fun fetchEvents(call: Call<EventResponse>) {
        _isLoading.value = true
        _errorMessage.value = null

        call.enqueue(object : Callback<EventResponse> {
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