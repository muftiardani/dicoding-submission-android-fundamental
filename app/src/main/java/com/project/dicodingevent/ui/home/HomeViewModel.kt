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
    companion object {
        private const val TAG = "HomeViewModel"
        private const val FINISHED_ID = "0"
        private const val UPCOMING_ID = "1"
    }

    // LiveData for upcoming events
    private val _listEvent = MutableLiveData<List<ListEventsItem>>()
    val listEvent: LiveData<List<ListEventsItem>> = _listEvent

    // LiveData for finished events
    private val _finishedEvent = MutableLiveData<List<ListEventsItem>>()
    val finishedEvent: LiveData<List<ListEventsItem>> = _finishedEvent

    // Loading states
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isRvLoading = MutableLiveData<Boolean>()
    val isRvLoading: LiveData<Boolean> = _isRvLoading

    // Error handling
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        fetchInitialData()
    }

    private fun fetchInitialData() {
        showUpcomingEvents()
        showFinishedEvents()
    }

    private fun showUpcomingEvents() {
        fetchEvents(
            eventId = UPCOMING_ID,
            setLoading = { _isLoading.value = it },
            onSuccess = { _listEvent.value = it },
            showError = true
        )
    }

    private fun showFinishedEvents() {
        fetchEvents(
            eventId = FINISHED_ID,
            setLoading = { _isRvLoading.value = it },
            onSuccess = { _finishedEvent.value = it },
            showError = false
        )
    }

    private fun fetchEvents(
        eventId: String,
        setLoading: (Boolean) -> Unit,
        onSuccess: (List<ListEventsItem>) -> Unit,
        showError: Boolean
    ) {
        setLoading(true)
        _errorMessage.value = null

        ApiConfig.getApiService().getEvents(eventId).enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                setLoading(false)
                handleResponse(response, onSuccess, showError)
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                handleError(t, setLoading, showError)
            }
        })
    }

    private fun handleResponse(
        response: Response<EventResponse>,
        onSuccess: (List<ListEventsItem>) -> Unit,
        showError: Boolean
    ) {
        if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {
                onSuccess(responseBody.listEvents ?: emptyList())
            } else if (showError) {
                _errorMessage.value = "Data tidak ditemukan"
            }
        } else {
            handleApiError(response, showError)
        }
    }

    private fun handleApiError(response: Response<EventResponse>, showError: Boolean) {
        val errorMessage = "Gagal memuat data: ${response.message()}"
        if (showError) {
            _errorMessage.value = errorMessage
        }
        Log.e(TAG, "API Error: $errorMessage")
    }

    private fun handleError(
        throwable: Throwable,
        setLoading: (Boolean) -> Unit,
        showError: Boolean
    ) {
        setLoading(false)
        val errorMessage = "Gagal memuat data: ${throwable.message}"
        if (showError) {
            _errorMessage.value = errorMessage
        }
        Log.e(TAG, "Network Error: ${throwable.message}")
    }
}