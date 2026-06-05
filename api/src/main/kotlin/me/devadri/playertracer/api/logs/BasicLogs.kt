@file:Suppress("unused")

package me.devadri.playertracer.api.logs

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.entity.Player

/**
 * Represents a log entry triggered when a player joins the server.
 */
@LogMetadata(id = "join", description = "Triggered when a player joins the server")
class JoinServerLog(player: Player) : AbstractLog("Joined the server", player)

/**
 * Represents a log entry triggered when a player leaves the server.
 */
@LogMetadata(id = "leave", description = "Triggered when a player leaves the server")
class LeaveServerLog(player: Player) : AbstractLog("Left the server", player)

/**
 * Represents a log entry triggered when a player sends a message on chat.
 */
@LogMetadata(id = "chat", description = "Triggered when a player sends a message into the chat")
class ChatLog(player: Player, chatMessage: String) : AbstractLog(chatMessage, player) {
    constructor(player: Player, chatMessage: Component) : this(player, LegacyComponentSerializer.legacyAmpersand().serialize(chatMessage))
}

/**
 * Represents a log entry triggered when a player executes a command.
 */
@LogMetadata(id = "command", description = "Triggered when a player tries to execute a command")
class CommandLog(player: Player, command: String) : AbstractLog(command, player)
