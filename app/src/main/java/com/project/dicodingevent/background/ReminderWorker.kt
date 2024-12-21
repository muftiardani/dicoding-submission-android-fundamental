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
import com.project.dicodingevent.data.retrofit.ApiConfig
import com.project.dicodingevent.data.response.EventResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "Dicoding Notification"
        private const val CHANNEL_NAME = "Daily Reminder Dicoding"
        private const val CHANNEL_DESCRIPTION = "Channel untuk notifikasi event terbaru"
        private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
        private const val TARGET_DATE_FORMAT = "EEEE, dd/MMMM/yyyy"
    }

    // LiveData
    private val latestEvent = MutableLiveData<String>()

    // Date Formatters
    private val apiDateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    private val targetDateFormat = SimpleDateFormat(TARGET_DATE_FORMAT, Locale("id", "ID"))

    override fun doWork(): Result {
        getLatestEvent()
        return Result.success()
    }

    private fun getLatestEvent() {
        ApiConfig.getApiService().getLatestEvent(-1, 1).enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                handleEventResponse(response)
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                sendNotification("Gagal mendapatkan event terbaru", "")
            }
        })
    }

    private fun handleEventResponse(response: Response<EventResponse>) {
        if (response.isSuccessful) {
            response.body()?.listEvents?.firstOrNull()?.let { event ->
                val eventName = event.name ?: "Tidak ada event"
                val formattedEventTime = formatDate(event.beginTime)

                latestEvent.postValue(eventName)
                sendNotification(eventName, formattedEventTime)
            }
        }
    }

    private fun sendNotification(eventName: String, eventTime: String?) {
        createNotificationChannel()

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = createNotificationBuilder(eventName, eventTime, pendingIntent)
        val notificationManager = getNotificationManager()

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createNotificationBuilder(
        eventName: String,
        eventTime: String?,
        pendingIntent: PendingIntent
    ): NotificationCompat.Builder =
        NotificationCompat.Builder(applicationContext, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.baseline_notifications_active_24)
            setContentTitle("Daftar $eventName")
            setContentText("Event akan dimulai pada $eventTime")
            setPriority(NotificationCompat.PRIORITY_DEFAULT)
            setContentIntent(pendingIntent)
            setAutoCancel(true)
        }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
            }
            getNotificationManager().createNotificationChannel(channel)
        }
    }

    private fun getNotificationManager(): NotificationManager =
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private fun formatDate(apiDate: String?): String {
        if (apiDate == null) return "Tanggal tidak valid"

        return try {
            val date = apiDateFormat.parse(apiDate)
            date?.let { targetDateFormat.format(it) } ?: "Tanggal tidak valid"
        } catch (e: Exception) {
            "Tanggal tidak valid"
        }
    }
}