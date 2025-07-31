package me.adrigamer2950.playertracer.commands.subcommands

import kotlinx.coroutines.Dispatchers
import me.adrigamer2950.adriapi.api.user.User
import me.adrigamer2950.playertracer.PlayerTracerPlugin
import me.adrigamer2950.playertracer.api.PlayerTracer
import me.adrigamer2950.playertracer.api.logs.Log
import me.adrigamer2950.playertracer.commands.AbstractPLCommand
import me.adrigamer2950.playertracer.commands.MainCommand
import me.adrigamer2950.playertracer.logs.LogQuery
import me.adrigamer2950.playertracer.util.Permission
import me.adrigamer2950.playertracer.util.TimeUtil
import me.adrigamer2950.playertracer.util.launchCoroutine
import org.bukkit.Bukkit
import java.sql.Timestamp
import java.util.*
import kotlin.reflect.KClass

class SearchSubCommand(val parent: MainCommand) : AbstractPLCommand("search", "Searches logs based on a query", listOf("s")) {

    companion object {
        val cache: MutableMap<UUID?, List<Log>> = mutableMapOf()

        val searching: MutableSet<UUID?> = mutableSetOf()
    }

    override fun getDisplayName(rootCommandName: String): String = "$rootCommandName search &c<query>"

    override fun execute(user: User, args: Array<out String>, commandName: String) {
        if (!Permission.SEARCH.isGrantedTo(user)) {
            user.sendMessage("&cYou don't have permission to use this command")
            return
        }

        if (args.isEmpty()) {
            user.sendMessage("&cUsage: /${getDisplayName(commandName)}")
            return
        }

        var uuids = mutableListOf<UUID>()
        val actions = mutableListOf<KClass<out Log>>()
        var after: Timestamp? = null
        var afterS: String? = null

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

                PlayerTracerPlugin.instance.logsProvider.getLogClassById(id)?.let {
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

                afterS = it.removePrefix("t:")
                after = TimeUtil.parseDuration(afterS)
            } else {
                user.sendMessage("&cInvalid query part: $it")
                return
            }
        }

        // Basic checks
        if (uuids.isEmpty()) {
            user.sendMessage("&cYou must specify a user with 'u:' prefix")
            return
        }

        if (actions.isEmpty()) {
            user.sendMessage("&cYou must specify at least one action with 'a:' prefix")
            return
        }

        val searcherUUID = if (user.isConsole()) null else user.getPlayerOrNull()!!.uniqueId

        if (searching.contains(searcherUUID)) {
            user.sendMessage("&cYou are already searching logs. Please wait for the previous search to finish")
            return
        }

        user.sendMessage(
            "&7Searching logs asynchronously for &6${uuids.size} &7player(s) with &6${actions.size} &7action(s)${if (afterS != null) " after &6$afterS" else ""}&7... This may take a while",
        )

        searching.add(searcherUUID)

        (plugin as PlayerTracer).getLogs(uuids.toTypedArray(), actions, after).thenAccept { results ->
            searching.remove(searcherUUID)

            if (results.isEmpty()) {
                user.sendMessage("&cNo data found")
                return@thenAccept
            }

            cache.put(searcherUUID, results)

            parent.subCommands.firstOrNull { it.info.name == "page" }?.execute(user, arrayOf("1"), commandName) ?: run {
                user.sendMessage("&cThere was an error trying to paginate the results. Pagination command not found")
            }
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
            PlayerTracerPlugin.instance.logsProvider.logs.map {
                PlayerTracerPlugin.instance.logsProvider.getId(it.`class`)
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