package com.project.dicodingevent.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.project.dicodingevent.R
import com.project.dicodingevent.data.remote.retrofit.ApiConfig

class ReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "reminder_channel"
        private const val PREFS_NAME = "reminder_prefs"
        private const val KEY_DAILY_REMINDER = "daily_reminder"
    }

    override suspend fun doWork(): Result {
        return try {
            if (isReminderActive()) {
                fetchAndShowEventNotification()
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private fun isReminderActive(): Boolean {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getBoolean(KEY_DAILY_REMINDER, false)
    }

    private suspend fun fetchAndShowEventNotification() {
        val apiService = ApiConfig.getApiService()
        val response = apiService.getEventReminder()

        if (!response.error && response.listEvents.isNotEmpty()) {
            val event = response.listEvents.first()
            showNotification(
                eventName = event.name,
                eventTime = event.beginTime,
                linkEvent = event.link
            )
        }
    }

    private fun showNotification(
        eventName: String,
        eventTime: String,
        linkEvent: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val pendingIntent = createPendingIntent(linkEvent)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle("Event Terdekat")
            .setContentText("Event: $eventName pada $eventTime")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createPendingIntent(linkEvent: String): PendingIntent {
        val notificationIntent = Intent(Intent.ACTION_VIEW, Uri.parse(linkEvent))
        return PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}