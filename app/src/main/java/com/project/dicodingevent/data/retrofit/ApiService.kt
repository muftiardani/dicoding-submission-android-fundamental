package com.project.dicodingevent.data.retrofit

import com.project.dicodingevent.data.response.DetailEventResponse
import com.project.dicodingevent.data.response.EventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    fun getEvents(
        @Query("active") id: String
    ): Call<EventResponse>

    @GET("events/{id}")
    fun getDetailEvent(
        @Path("id") id: String
    ): Call<DetailEventResponse>

    @GET("events")
    fun searchEvents(@Query("q") query: String): Call<EventResponse>

    @GET("events")
    fun getLatestEvent(
        @Query("active") active: Int = -1,  // Default value = -1
        @Query("limit") limit: Int = 1      // Default value = 1
    ): Call<EventResponse>
}