package com.project.dicodingevent.ui.home

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

class HomeViewModel : ViewModel() {

    private val _listEvent = MutableLiveData<List<ListEventsItem>>()
    val listEvent: LiveData<List<ListEventsItem>> = _listEvent

    private val _finishedEvent = MutableLiveData<List<ListEventsItem>>()
    val finishedEvent: LiveData<List<ListEventsItem>> = _finishedEvent

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isRvLoading = MutableLiveData<Boolean>()
    val isRvLoading: LiveData<Boolean> = _isRvLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    companion object {
        private const val TAG = "HomeViewModel"
        private const val FINISHED_ID = "0"
        private const val UPCOMING_ID = "1"
    }

    init {
        showUpcomingEvents()
        showFinishedEvents()
    }

    private fun showUpcomingEvents() {
        _isLoading.value = true
        _errorMessage.value = null
        val client = ApiConfig.getApiService().getEvents(UPCOMING_ID)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _listEvent.value = response.body()?.listEvents
                    } else {
                        _errorMessage.value = "Data tidak ditemukan"
                    }
                } else {
                    _errorMessage.value = "Gagal memuat data: ${response.message()}"
                    Log.e(TAG, "onResponse: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Gagal memuat data: ${t.message}"
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun showFinishedEvents() {
        _isRvLoading.value = true
        _errorMessage.value = null
        val client = ApiConfig.getApiService().getEvents(FINISHED_ID)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isRvLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _finishedEvent.value = response.body()?.listEvents
                    }
                } else {
                    Log.e(TAG, "onResponse: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isRvLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}