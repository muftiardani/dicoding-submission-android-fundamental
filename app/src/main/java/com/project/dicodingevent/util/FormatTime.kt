package com.project.dicodingevent.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FormatTime {

    companion object {
        fun formatWithHour(time: String?): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH:mm EEEE, dd/MM/yyyy", Locale("id", "ID"))
            val date: Date? = inputFormat.parse(time.toString())

            return if (date != null) {
                outputFormat.format(date)
            } else {
                ""
            }
        }

        fun formatDateOnly(time: String?): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("EEEE, dd/MM/yyyy", Locale("id", "ID"))
            val date: Date? = inputFormat.parse(time.toString())

            return if (date != null) {
                outputFormat.format(date)
            } else {
                ""
            }
        }

        fun getHour(time: String?): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH:mm", Locale("id", "ID"))
            val date: Date? = inputFormat.parse(time.toString())

            return if (date != null) {
                outputFormat.format(date)
            } else {
                ""
            }
        }
    }
}