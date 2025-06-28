@file:Suppress("unused")

package me.adrigamer2950.playerlogs.logs

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import java.sql.Timestamp
import java.time.Instant
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

/**
 * Represents a log entry.
 * It MUST have a static field called `id` which is a unique identifier to use when querying logs
 */
abstract class Log(val message: String, player: OfflinePlayer, val timestamp: Long) {
    constructor(message: String, player: OfflinePlayer) : this(message, player, Timestamp.from(Instant.now()).time)

    val playerUUID = player.uniqueId // Player's UUID
}

/**
 * Represents a log entry relating a player joining the server.
 */
class JoinServerLog(player: Player) : Log("Joined the server", player) {

    companion object {
        const val ID = "join"
    }
}

/**
 * Represents a log entry relating a player leaving the server.
 */
class LeaveServerLog(player: Player) : Log("Left the server", player) {

    companion object {
        const val ID = "leave"
    }
}

/**
 * Represents a log entry relating a player chatting.
 */
class ChatLog(player: Player, chatMessage: String) : Log("Chat: $chatMessage", player) {
    constructor(player: Player, chatMessage: Component) : this(player, LegacyComponentSerializer.legacyAmpersand().serialize(chatMessage))

    companion object {
        const val ID = "chat"
    }
}

/**
 * Represents a log entry relating a player executing a command.
 */
class CommandLog(player: Player, command: String) : Log("Executed command: $command", player) {

    companion object {
        const val ID = "command"
    }
}
