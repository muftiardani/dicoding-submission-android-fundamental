package com.project.dicodingevent.util

import java.text.SimpleDateFormat
import java.util.Locale

object FormatTime {
    // Constants
    private const val INPUT_FORMAT = "yyyy-MM-dd HH:mm:ss"
    private const val DATE_TIME_FORMAT = "HH:mm EEEE, dd/MM/yyyy"
    private const val DATE_ONLY_FORMAT = "EEEE, dd/MM/yyyy"
    private const val TIME_ONLY_FORMAT = "HH:mm"

    // Locale settings
    private val defaultLocale = Locale.getDefault()
    private val indonesianLocale = Locale("id", "ID")

    // Date formatters
    private val inputFormatter = SimpleDateFormat(INPUT_FORMAT, defaultLocale)
    private val dateTimeFormatter = SimpleDateFormat(DATE_TIME_FORMAT, indonesianLocale)
    private val dateOnlyFormatter = SimpleDateFormat(DATE_ONLY_FORMAT, indonesianLocale)
    private val timeOnlyFormatter = SimpleDateFormat(TIME_ONLY_FORMAT, indonesianLocale)

    fun formatWithHour(time: String?): String =
        parseAndFormat(time, dateTimeFormatter)

    fun formatDateOnly(time: String?): String =
        parseAndFormat(time, dateOnlyFormatter)

    fun getHour(time: String?): String =
        parseAndFormat(time, timeOnlyFormatter)

    private fun parseAndFormat(time: String?, outputFormatter: SimpleDateFormat): String {
        if (time == null) return ""

        return try {
            val date = inputFormatter.parse(time)
            date?.let { outputFormatter.format(it) } ?: ""
        } catch (e: Exception) {
            ""
        }
    }
}