package me.adrigamer2950.playerlogs.util

import java.sql.Timestamp
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object TimeUtil {

    const val ONE_MINUTE = 60
    const val ONE_HOUR = 60 * ONE_MINUTE
    const val ONE_DAY = 24 * ONE_HOUR
    const val ONE_WEEK = 7 * ONE_DAY
    const val ONE_MONTH = 30 * ONE_DAY // Approximation, as months vary in length
    const val ONE_YEAR = 365 * ONE_DAY // Approximation, as years can be leap years

    // Parses humanized duration (1d, 1w, 50s) into timestamps
    // based on the current timestamp (The current timestamp is subtracted from the parsed timestamp)
    fun parseDuration(duration: String): Timestamp {
        val regex = Regex("(\\d+)([smhdw])") // Allows seconds (s), minutes (m), hours (h), days (d), and weeks (w)
        val match = regex.matchEntire(duration.lowercase()) ?: throw IllegalArgumentException("Invalid format: $duration")

        val (amountStr, unit) = match.destructured
        val amount = amountStr.toLong()

        return Timestamp.from(
            Instant.now().minus(
                when (unit) {
                    "s" -> Duration.ofSeconds(amount)
                    "m" -> Duration.ofMinutes(amount)
                    "h" -> Duration.ofHours(amount)
                    "d" -> Duration.ofDays(amount)
                    "w" -> Duration.ofDays(amount * 7)
                    else -> throw IllegalArgumentException("Unrecognised time unit: $unit")
                }
            )
        )
    }

    // Parses a timestamp into text (x seconds ago, x minutes ago, etc.)
    fun formatTimeAgo(timestamp: Long): String {
        val timestamp = Instant.ofEpochMilli(timestamp)
        val now = Instant.now()
        val seconds = Duration.between(timestamp, now).seconds

        return when {
            seconds < ONE_MINUTE -> "$seconds second${if (seconds != 1L) "s" else ""} ago"
            seconds < ONE_HOUR -> {
                val minutes = seconds / ONE_MINUTE
                "$minutes minute${if (minutes != 1L) "s" else ""} ago"
            }
            seconds < ONE_DAY -> {
                val hours = seconds / ONE_HOUR
                "$hours hour${if (hours != 1L) "s" else ""} ago"
            }
            seconds < ONE_WEEK -> {
                val days = seconds / ONE_DAY
                "$days day${if (days != 1L) "s" else ""} ago"
            }
            seconds < ONE_MONTH -> {
                val weeks = seconds / ONE_WEEK
                "$weeks week${if (weeks != 1L) "s" else ""} ago"
            }
            seconds < ONE_YEAR -> {
                val months = seconds / ONE_MONTH
                "$months month${if (months != 1L) "s" else ""} ago"
            }
            else -> {
                val years = seconds / ONE_YEAR
                "$years year${if (years != 1L) "s" else ""} ago"
            }
        }
    }

    // Parses a timestamp to a formatted date string
    fun timestampToDate(timestamp: Long): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a")
        return Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .format(formatter)
    }
}