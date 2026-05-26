package me.devadri.playertracer.database

import me.devadri.playertracer.api.location.Location
import me.devadri.playertracer.api.logs.Log
import java.util.UUID

/**
 * Used to notify player that something went wrong when retrieving logs from the database
 *
 * Example: There was an error decoding a log
 */
class DatabaseFailureLog(override val message: String) : Log {

    override val playerUUID: UUID
        get() = throw IllegalAccessException("You shouldn't use this.")
    override val timestamp: Long
        get() = throw IllegalAccessException("I'm serious, just don't.")
    override val location: Location
        get() = throw IllegalAccessException("...")
}