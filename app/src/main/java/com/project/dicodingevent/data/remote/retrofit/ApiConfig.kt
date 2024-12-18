package com.project.dicodingevent.data.remote.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig private constructor() {
    companion object {
        private const val BASE_URL = "https://event-api.dicoding.dev/"

        @Volatile
        private var apiService: ApiService? = null

        fun getApiService(): ApiService =
            apiService ?: synchronized(this) {
                apiService ?: createApiService().also { apiService = it }
            }

        private fun createApiService(): ApiService {
            val loggingInterceptor = HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}