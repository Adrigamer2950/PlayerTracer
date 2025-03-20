package me.adrigamer2950.playerlogs.logs

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import java.sql.Timestamp
import java.time.Instant
import kotlinx.serialization.Serializable
import org.bukkit.OfflinePlayer

typealias Serializable = Serializable

/**
 * Represents a log entry.
 * Every subclass should have the [Serializable] annotation.
 */
@Serializable
sealed class Log(val logMessage: String, val player: OfflinePlayer?, val timestamp: Long) {
    constructor(logMessage: String, player: OfflinePlayer?) : this(logMessage, player, Timestamp.from(Instant.now()).time)
}

/**
 * Represents a log entry relating a player joining the server.
 */
@Serializable
data class JoinServerLog(private val _player: OfflinePlayer) : Log("Joined the server", _player)

/**
 * Represents a log entry relating a player leaving the server.
 */
@Serializable
data class LeaveServerLog(private val _player: OfflinePlayer) : Log("Leaved the server", _player)

/**
 * Represents a log entry relating a player chatting.
 */
@Serializable
data class ChatLog(private val _player: OfflinePlayer, val message: String) : Log("Chat: $message", _player) {
    constructor(player: OfflinePlayer, chatMessage: Component) : this(player, LegacyComponentSerializer.legacyAmpersand().serialize(chatMessage))
}

/**
 * Represents a log entry relating a player executing a command.
 */
@Serializable
data class CommandLog(private val _player: OfflinePlayer, val command: String) : Log("Executed command: $command", _player)
