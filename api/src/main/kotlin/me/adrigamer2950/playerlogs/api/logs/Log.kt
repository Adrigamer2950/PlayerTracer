package me.adrigamer2950.playerlogs.api.logs

import org.bukkit.OfflinePlayer
import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

/**
 * Represents a log entry.
 * It MUST have a static field called `metadata` of type [LogInfo] that contains basic metadata about the log
 */
interface Log {

    val message: String
    val playerUUID: UUID
    val timestamp: Long
}

/**
 * Abstract implementation of [Log].
 * Implementations of this class MUST have a static field called `metadata` of type [LogInfo] that contains basic metadata about the log
 */
abstract class AbstractLog(override val message: String, player: OfflinePlayer, override val timestamp: Long) : Log {

    constructor(message: String, player: OfflinePlayer) : this(message, player, Timestamp.from(Instant.now()).time)

    override val playerUUID = player.uniqueId // Player's UUID
}