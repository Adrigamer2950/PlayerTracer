package me.adrigamer2950.playertracer.query

import me.adrigamer2950.playertracer.PlayerTracerPlugin
import me.adrigamer2950.playertracer.api.logs.Log
import me.adrigamer2950.playertracer.util.TimeUtil
import me.devadri.obsidian.user.User
import org.bukkit.Bukkit
import java.sql.Timestamp
import java.util.UUID
import kotlin.reflect.KClass

object QueryHandler {

    @JvmStatic
    internal fun handleQuery(user: User, args: Array<out String>): LogQuery? {
        val uuids = mutableListOf<UUID>()
        val actions = mutableListOf<KClass<out Log>>()
        var after: Timestamp? = null
        var afterS: String? = null

        // Parse args into UUIDs and actions to search for
        args.forEach {
            if (it.startsWith("u:")) {
                val playerInfo = it.removePrefix("u:") // Name or UUID
                val possibleUUID = try { UUID.fromString(playerInfo) } catch (_: IllegalArgumentException) { null }

                // If [playerInfo] is a valid UUID, find the player by UUID
                val player = if (possibleUUID != null) {
                    Bukkit.getOfflinePlayer(possibleUUID)
                } else {
                    // Otherwise, find the player by name if it's cached (has joined the server before) or is online
                    Bukkit.getPlayer(playerInfo) ?: Bukkit.getOfflinePlayerIfCached(playerInfo)
                }

                // Check if player has been found. If not, send an error to the user
                if (player == null || (!player.hasPlayedBefore() && !player.isOnline)) {
                    user.sendMessage("&cNo player found with name/uuid: &6$playerInfo&c. Please check if the player has joined the server before")
                    return null
                }

                uuids.add(player.uniqueId)
            } else if (it.startsWith("a:")) {
                val id = it.removePrefix("a:")

                PlayerTracerPlugin.instance.logsProvider.getLogClassById(id)?.let { klass ->
                    actions.add(klass)
                } ?: run {
                    user.sendMessage("&cNo action found with ID $id")
                    return null
                }
            } else if (it.startsWith("t:")) {
                if (after != null) {
                    user.sendMessage("&cYou can only use the 't:' prefix once in a query")
                    return null
                }

                afterS = it.removePrefix("t:")
                after = TimeUtil.parseDuration(afterS)
            } else {
                user.sendMessage("&cInvalid query part: $it")
                return null
            }
        }

        // Basic checks
        if (uuids.isEmpty()) {
            user.sendMessage("&cYou must specify a user with 'u:' prefix")
            return null
        }

        if (actions.isEmpty()) {
            user.sendMessage("&cYou must specify at least one action with 'a:' prefix")
            return null
        }

        return LogQuery(uuids.toTypedArray(), actions, after, afterS)
    }
}