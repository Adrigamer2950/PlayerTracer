package me.adrigamer2950.playerlogs.util

import java.sql.Timestamp
import java.time.Duration
import java.time.Instant

object TimeUtil {

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
}