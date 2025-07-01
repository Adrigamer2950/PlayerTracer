package me.adrigamer2950.playerlogs.commands.subcommands

import me.adrigamer2950.adriapi.api.user.User
import me.adrigamer2950.playerlogs.PlayerLogsPlugin
import me.adrigamer2950.playerlogs.commands.AbstractPLCommand
import me.adrigamer2950.playerlogs.logs.Log
import me.adrigamer2950.playerlogs.logs.LogQuery
import me.adrigamer2950.playerlogs.util.TimeUtil
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import java.sql.Timestamp
import java.util.*
import kotlin.reflect.KClass

// TODO: Implement a paging system for results
class SearchSubCommand : AbstractPLCommand("search", "Searches logs based on a query", listOf("s")) {

    override fun getDisplayName(rootCommandName: String): String = "$rootCommandName search &c<query>"

    override fun execute(user: User, args: Array<out String>, commandName: String) {
        if (args.isEmpty()) {
            user.sendMessage("&cUsage: /${getDisplayName(commandName)}")
            return
        }

        var uuids = mutableListOf<UUID>()
        val actions = mutableListOf<KClass<out Log>>()
        var after: Timestamp? = null

        // Parse args into UUIDs and actions to search for
        args.forEach {
            if (it.startsWith("u:")) {
                val playerInfo = it.removePrefix("u:") // Name or UUID
                val possibleUUID = getPlayerUuidFromString(playerInfo)

                // If [playerInfo] is a valid UUID, find the player by UUID
                val player = if (possibleUUID != null) {
                    Bukkit.getOfflinePlayer(possibleUUID)
                } else {
                    // Otherwise, find the player by name if it's cached (has joined the server before)
                    Bukkit.getOfflinePlayerIfCached(playerInfo)
                }

                // Check if player has been found. If not, send an error to the user
                if (player == null || !player.hasPlayedBefore()) {
                    user.sendMessage("&cNo player found with name/uuid: &6$playerInfo&c. Please check if the player has joined the server before")
                    return
                }

                uuids.add(player.uniqueId)
            } else if (it.startsWith("a:")) {
                val id = it.removePrefix("a:")

                PlayerLogsPlugin.instance.logsProvider.getLogClassById(id)?.let {
                    actions.add(it)
                } ?: run {
                    user.sendMessage("&cNo action found with ID $id")
                    return
                }
            } else if (it.startsWith("t:")) {
                if (after != null) {
                    user.sendMessage("&cYou can only use the 't:' prefix once in a query")
                    return
                }

                after = TimeUtil.parseDuration(it.removePrefix("t:"))
            } else {
                user.sendMessage("&cInvalid query part: $it")
                return
            }
        }

        // Basic checks
        if (uuids.isEmpty()) {
            user.sendMessage("&cYou must specify a user with 'u:' prefix.")
            return
        }

        if (actions.isEmpty()) {
            user.sendMessage("&cYou must specify at least one action with 'a:' prefix.")
            return
        }

        // Get results
        val results = LogQuery(uuids.toTypedArray(), actions, after).getResults()

        if (results.isEmpty()) {
            user.sendMessage("&cNo data found")
            return
        }

        // Display results to the user
        user.sendMessage("&7--------- &bSearch Results &7---------")
        results.forEach {
            val mm = MiniMessage.miniMessage()
            user.sendMessage(mm.deserialize("<hover:show_text:'${TimeUtil.timestampToDate(it.timestamp)}'><gray>[${TimeUtil.formatTimeAgo(it.timestamp)}]</hover> " +
                    "<aqua>${Bukkit.getOfflinePlayer(it.playerUUID).name}<gray>: ${it.message}"))
        }
    }

    override fun tabComplete(user: User, args: Array<out String>, commandName: String): List<String> {
        val queries = listOf("u:", "a:", "t:") // User (nick or uuid), Action, Time

        /*
         * If no args are provided, the last arg is empty or the last arg is any recognized query type, return all types of queries
         * If using 'u:' prefix, return online players
         * If using 'a:' prefix, return all available actions
         * If using 't:' prefix, return example time queries
         * Otherwise, just return the default tab completion
         *
         * All of this is filtered later to include results that start with the last arg
         */
        return if (args.isEmpty() || args.last()
                .isEmpty() || queries.any { it.startsWith(args.last()) && it != args.last() }
        ) {
            queries
        } else if (args.last().startsWith("u:")) {
            Bukkit.getOnlinePlayers().map { "u:${it.name}" }
        } else if (args.last().startsWith("a:")) {
            PlayerLogsPlugin.instance.logsProvider.logs.map {
                PlayerLogsPlugin.instance.logsProvider.getId(it.`class`)
            }.map { "a:$it" }
        } else if (args.last().startsWith("t:")) {
            listOf("1m", "1h", "1d", "1w").map { "t:$it" }
        } else {
            super.tabComplete(user, args, commandName)
        }.filter { it.startsWith(args.last()) }
    }

    fun getPlayerUuidFromString(uuid: String): UUID? {
        return try {
            UUID.fromString(uuid)
        } catch (_: IllegalArgumentException) {
            null
        }
    }
}