package com.lens.taskmanager.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    // Function to convert dueDate string to Date object
    fun getDueDateAsDate(dueDate:String): Date? {
        return try {
            val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            format.parse(dueDate)
        } catch (e: Exception) {
            null
        }
    }
}
