@file:Suppress("unused")

package me.adrigamer2950.playertracer.logs

import me.adrigamer2950.playertracer.api.logs.AbstractLog
import me.adrigamer2950.playertracer.api.logs.LogInfo
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.entity.Player

/**
 * Represents a log entry relating a player joining the server.
 */
class JoinServerLog(player: Player) : AbstractLog("Joined the server", player) {

    companion object {
        @JvmField
        val metadata = LogInfo("join", "Triggered when a player connects to the server")
    }
}

/**
 * Represents a log entry relating a player leaving the server.
 */
class LeaveServerLog(player: Player) : AbstractLog("Left the server", player) {

    companion object {
        @JvmField
        val metadata = LogInfo("leave", "Triggered when a player quits the server")
    }
}

/**
 * Represents a log entry relating a player chatting.
 */
class ChatLog(player: Player, chatMessage: String) : AbstractLog("Chat: $chatMessage", player) {
    constructor(player: Player, chatMessage: Component) : this(player, LegacyComponentSerializer.legacyAmpersand().serialize(chatMessage))

    companion object {
        @JvmField
        val metadata = LogInfo("chat", "Triggered when a player sends a message into the chat")
    }
}

/**
 * Represents a log entry relating a player executing a command.
 */
class CommandLog(player: Player, command: String) : AbstractLog("Executed command: $command", player) {

    companion object {
        @JvmField
        val metadata = LogInfo("command", "Triggered when a player tries to execute a command")
    }
}
