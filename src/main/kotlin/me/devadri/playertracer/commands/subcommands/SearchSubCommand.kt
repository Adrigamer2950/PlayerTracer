package me.devadri.playertracer.commands.subcommands

import me.devadri.obsidian.user.User
import me.devadri.playertracer.PlayerTracerPlugin
import me.devadri.playertracer.api.logs.Log
import me.devadri.playertracer.commands.AbstractPLCommand
import me.devadri.playertracer.commands.MainCommand
import me.devadri.playertracer.gui.LogResultsGUI
import me.devadri.playertracer.query.QueryHandler
import me.devadri.playertracer.util.Permission
import me.devadri.playertracer.viewmode.ViewMode
import me.devadri.playertracer.viewmode.ViewModeManager
import me.devadri.obsidian.asPlayer
import me.devadri.obsidian.isConsole
import org.bukkit.Bukkit
import java.util.*

class SearchSubCommand(val parent: MainCommand) : AbstractPLCommand("search", "Searches logs based on a query", listOf("s")) {

    companion object {
        val cache: MutableMap<UUID?, Pair<List<Log>, Boolean /* Search query only has 1 log type */>> = mutableMapOf()

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

        val query = QueryHandler.handleQuery(user, args) ?: return

        val searcherUUID = if (user.isConsole()) null else user.asPlayer()!!.uniqueId

        if (searching.contains(searcherUUID)) {
            user.sendMessage("&cYou are already searching logs. Please wait for the previous search to finish")
            return
        }

        user.sendMessage(
            "&7Searching logs asynchronously for &6${query.uuids.size} &7player(s) with &6${query.actions.size} &7action(s)${if (query.afterS != null) " after &6${query.afterS}" else ""}&7... This may take a while",
        )

        searching.add(searcherUUID)

        // Search logs asynchronously
        plugin.getLogsWithFuture(query).thenAccept { results ->
            if (results.isEmpty()) {
                user.sendMessage("&cNo data found")
                return@thenAccept
            }

            when (ViewModeManager.get(searcherUUID)) {
                ViewMode.GUI -> {
                    // Use main thread to open inventory
                    plugin.scheduler.sync().run {
                        LogResultsGUI(results).openFor(user.asPlayer()!!)
                    }
                }
                ViewMode.CHAT -> {
                    cache[searcherUUID] = Pair(
                        results,
                        query.actions.size == 1
                    )

                    // Execute '/playertracer page 1'
                    parent.subCommands.firstOrNull { it.info.name == "page" }?.execute(user, arrayOf("1"), commandName) ?: run {
                        user.sendMessage("&cThere was an error trying to paginate the results. Pagination command not found")
                    }
                }
            }
        }.exceptionally {
            user.sendMessage("&cAn error occurred while searching logs: ${it.message}")
            plugin.logger.error("&cAn error occurred while searching logs", it)
            null
        }

        searching.remove(searcherUUID)
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
        return when {
            args.isEmpty() || args.last()
                .isEmpty() || queries.any { it.startsWith(args.last()) && it != args.last() }
                -> {
                queries
            }

            args.last().startsWith("u:") -> {
                Bukkit.getOnlinePlayers().map { "u:${it.name}" }
            }

            args.last().startsWith("a:") -> {
                PlayerTracerPlugin.instance.logsProvider.logs.map {
                    PlayerTracerPlugin.instance.logsProvider.getId(it.clazz)
                }.map { "a:$it" }
            }

            args.last().startsWith("t:") -> {
                listOf("1m", "1h", "1d", "1w").map { "t:$it" }
            }

            else -> {
                super.tabComplete(user, args, commandName)
            }
        }.filter { it.startsWith(args.lastOrNull() ?: return@filter true) }
    }
}