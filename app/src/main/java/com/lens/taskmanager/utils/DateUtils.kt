package com.lens.taskmanager.utils

import android.os.Build
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object DateUtils {
    fun getFormattedTime(timestamp: String): String {
        // Define the input date format
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(timestamp) ?: return ""
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getTimeZone("UTC")
        return outputFormat.format(date).uppercase(Locale.US)
    }

    fun getFormattedDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date: Date = inputFormat.parse(dateString) ?: return dateString
        return outputFormat.format(date)
    }

    fun getCurrentFullTimeFormatted(): String {
        val dateFormat = SimpleDateFormat("M/d/yyyy, h:mm:ss a", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    fun formatDateIntoDay(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        return outputFormat.format(date)
    }

    fun isToday(dateString: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val givenDate: Date = dateFormat.parse(dateString)
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val currentDate = calendar.time
        val dateOnlyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val givenDateString = dateOnlyFormat.format(givenDate)
        val currentDateString = dateOnlyFormat.format(currentDate)
        return givenDateString == currentDateString
    }

    fun calculateDaysBetween(startDate: String, endDate: String): Long {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val start = dateFormat.parse(startDate)
        val end = dateFormat.parse(endDate)
        val diff = end.time - start.time
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1
    }

    fun getCurrentYear(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.YEAR)
    }

    fun formatDateIntoReadable(date: String): String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        date.formatIso8601IntoReadable()
    } else {
        date // TODO: give solution for API level <26
    }

    fun formatReadableIntoIso8601(date: String): String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        date.formatReadableIntoIso8601()
    } else {
        date // TODO: give solution for API level <26
    }
}
