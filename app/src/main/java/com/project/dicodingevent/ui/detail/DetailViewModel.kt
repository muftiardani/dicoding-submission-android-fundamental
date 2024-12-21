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
    private val mFavoriteEventRepository: FavoriteEventRepository =
        FavoriteEventRepository(application)

    private val _event = MutableLiveData<Event>()
    val event: LiveData<Event> = _event

    private val _favoriteEvent = mFavoriteEventRepository.getFavoriteEventById(eventId)
    val favoriteEvent: LiveData<FavoriteEvent> = _favoriteEvent

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    companion object {
        private const val TAG = "UpcomingDetailViewModel"
    }

    init {
        findEvent(eventId)
    }

    private fun findEvent(eventId: String) {
        _isLoading.value = true
        _errorMessage.value = null
        val client = ApiConfig.getApiService().getDetailEvent(eventId)
        client.enqueue(object : Callback<DetailEventResponse> {
            override fun onResponse(
                call: Call<DetailEventResponse>,
                response: Response<DetailEventResponse>
            ) {
                _isLoading.value = false
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

            override fun onFailure(call: Call<DetailEventResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Gagal memuat data: ${t.message}"
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }


    fun insert(favoriteEvent: FavoriteEvent) {
        mFavoriteEventRepository.insert(favoriteEvent)
    }

    fun deleteFavorite(favoriteEvent: FavoriteEvent) {
        mFavoriteEventRepository.delete(favoriteEvent)
    }
}