package me.adrigamer2950.playerlogs.logs

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import java.sql.Timestamp
import java.time.Instant
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.UUID

/**
 * Represents a log entry.
 */
abstract class Log(val message: String, player: OfflinePlayer, val timestamp: Long) {
    constructor(message: String, player: OfflinePlayer) : this(message, player, Timestamp.from(Instant.now()).time)

    val playerUUID = player.uniqueId // Player's UUID
}

/**
 * Represents a log entry relating a player joining the server.
 */
class JoinServerLog(player: Player) : Log("Joined the server", player)

/**
 * Represents a log entry relating a player leaving the server.
 */
class LeaveServerLog(player: Player) : Log("Leaved the server", player)

/**
 * Represents a log entry relating a player chatting.
 */
class ChatLog(player: Player, chatMessage: String) : Log("Chat: $chatMessage", player) {
    constructor(player: Player, chatMessage: Component) : this(player, LegacyComponentSerializer.legacyAmpersand().serialize(chatMessage))
}

/**
 * Represents a log entry relating a player executing a command.
 */
class CommandLog(player: Player, command: String) : Log("Executed command: $command", player)
