package com.lens.taskmanager.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

fun String?.capitalizeFirstLetter(): String {
    return this?.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
    }?.lowercase(Locale.ROOT)?.replaceFirstChar {
        it.titlecase(Locale.ROOT)
    } ?: ""
}

fun String.stripHtmlTags(): String {
    return this.replace(Regex("<.*?>"), "")
}

fun String.truncate(maxLength: Int): String {
    if (this.length <= maxLength) {
        return this
    }
    return this.substring(0, maxLength) + "..."
}

fun String.isAlphaNumeric(allowedCharacters: Array<Char>? = null): Boolean {
    return this.matches("^[a-zA-Z0-9${allowedCharacters?.joinToString("") ?: ""}]+$".toRegex())
}

@RequiresApi(Build.VERSION_CODES.O)
fun String.formatIso8601IntoReadable(): String {
    // Define the input and output date formats
    val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val outputFormat = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")

    val inputDateTime = try {
        // Parse the input date string
        LocalDateTime.parse(this, inputFormat)
    } catch (e: DateTimeParseException) {
        LocalDateTime.MIN
    }

    // Convert to the desired time zone (e.g., Asia/Kolkata for India)
    val localTimeZone = ZoneId.systemDefault()
    val zonedDateTime = ZonedDateTime
        .of(inputDateTime, ZoneId.of("UTC"))
        .withZoneSameInstant(localTimeZone)

    // Format the date in the desired output format
    return zonedDateTime.format(outputFormat)
}

@RequiresApi(Build.VERSION_CODES.O)
fun String.formatReadableIntoIso8601(): String {
    // Define the input and output date formats
    val inputFormat = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")
    val outputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

    // Parse the input date string
    val inputDateTime = LocalDateTime.parse(this, inputFormat)

    val zonedDateTime = ZonedDateTime.of(inputDateTime, ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"))

    // Format the date in the desired output format
    return outputFormat.format(zonedDateTime)
}
