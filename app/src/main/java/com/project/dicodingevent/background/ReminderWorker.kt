package com.project.dicodingevent.background

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.project.dicodingevent.MainActivity
import com.project.dicodingevent.R
import com.project.dicodingevent.data.retrofit.ApiService
import com.project.dicodingevent.data.response.EventResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReminderWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    private val latestEvent = MutableLiveData<String>()

    override fun doWork(): Result {
        getLatestEvent()
        return Result.success()
    }

    private fun getLatestEvent() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://event-api.dicoding.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val call = apiService.getLatestEvent(-1, 1)
        call.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    val events = response.body()?.listEvents
                    val eventName = events?.firstOrNull()?.name ?: "Tidak ada event"
                    val eventTime = events?.firstOrNull()?.beginTime
                    val formattedEventTime = formatDate(eventTime)

                    latestEvent.postValue(eventName)
                    sendNotification(eventName, formattedEventTime)
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                sendNotification("Gagal mendapatkan event terbaru", "")
            }
        })
    }

    private fun sendNotification(eventName: String, eventTime: String?) {
        // Membuat channel notifikasi
        createNotificationChannel()

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_notifications_active_24)
            .setContentTitle("Daftar $eventName")
            .setContentText("Event akan dimulai pada $eventTime")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME,NotificationManager.IMPORTANCE_HIGH)
            builder.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun createNotificationChannel() {
        // Membuat channel notifikasi jika API >= 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_NAME
            val descriptionText = "Channel untuk notifikasi event terbaru"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun formatDate(apiDate: String?): String {
        val apiDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val date: Date? = apiDateFormat.parse(apiDate)

        val targetFormat = SimpleDateFormat("EEEE, dd/MMMM/yyyy", Locale("id", "ID"))

        return date?.let {
            targetFormat.format(it)
        } ?: "Tanggal tidak valid"
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "Dicoding Notification"
        const val CHANNEL_NAME = "Daily Reminder Dicoding"
    }
}
