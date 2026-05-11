@file:Suppress("unused")

package me.devadri.playertracer.logs

import me.devadri.playertracer.api.logs.AbstractLog
import me.devadri.playertracer.api.logs.LogMetadata
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.entity.Player

/**
 * Represents a log entry relating a player joining the server.
 */
@LogMetadata(id = "join", description = "Triggered when a player connects to the server")
class JoinServerLog(player: Player) : AbstractLog("Joined the server", player)

/**
 * Represents a log entry relating a player leaving the server.
 */
@LogMetadata(id = "leave", description = "Triggered when a player quits the server")
class LeaveServerLog(player: Player) : AbstractLog("Left the server", player)

/**
 * Represents a log entry relating a player chatting.
 */
@LogMetadata(id = "chat", description = "Triggered when a player sends a message into the chat")
class ChatLog(player: Player, chatMessage: String) : AbstractLog(chatMessage, player) {
    constructor(player: Player, chatMessage: Component) : this(player, LegacyComponentSerializer.legacyAmpersand().serialize(chatMessage))
}

/**
 * Represents a log entry relating a player executing a command.
 */
@LogMetadata(id = "command", description = "Triggered when a player tries to execute a command")
class CommandLog(player: Player, command: String) : AbstractLog(command, player)
