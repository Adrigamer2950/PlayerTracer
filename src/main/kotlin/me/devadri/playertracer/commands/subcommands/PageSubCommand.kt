package me.devadri.playertracer.commands.subcommands

import me.devadri.obsidian.user.User
import me.devadri.playertracer.commands.AbstractPLCommand
import me.devadri.playertracer.util.Constants.PAGE_SIZE
import me.devadri.playertracer.util.Permission
import me.devadri.playertracer.util.TimeUtil
import me.devadri.playertracer.util.miniMessage
import me.devadri.obsidian.asPlayer
import me.devadri.obsidian.isConsole
import net.kyori.adventure.text.event.ClickEvent

class PageSubCommand : AbstractPLCommand("page", "Shows the specified page of the current search", listOf("p")) {

    override fun getDisplayName(rootCommandName: String): String = "$rootCommandName page &c<page>"

    override fun execute(
        user: User,
        args: Array<out String>,
        commandName: String
    ) {
        val pair = SearchSubCommand.cache[if (user.isConsole()) null else user.asPlayer()!!.uniqueId] ?: run {
            user.sendMessage("&cNo search results found. Please run a search first.")
            return
        }

        val logs = pair.first
        val onlyOneType = pair.second

        if (args.isEmpty()) {
            user.sendMessage("&cUsage: /${getDisplayName(commandName)}")
            return
        }

        val pageNumber = args[0].toIntOrNull() ?: run {
            user.sendMessage("&cInvalid page number. Please provide a valid integer.")
            return
        }

        val totalPages = (logs.size / PAGE_SIZE) + if (logs.size % PAGE_SIZE > 0) 1 else 0

        if (pageNumber !in 1..totalPages) {
            user.sendMessage("&cInvalid page number. Please provide a number between 1 and $totalPages")
            return
        }

        val pagedLogs = logs.subList(
            0,
            if (pageNumber * PAGE_SIZE > logs.size) logs.size else pageNumber * PAGE_SIZE
        )

        // Display results to the user
        user.sendMessage("&7--------- &bSearch Results &7---------")
        user.sendMessage("&7Page $pageNumber of $totalPages")
        pagedLogs.forEach {
            val time = "[${TimeUtil.formatTimeAgo(it.timestamp)}]"
            val displayName = "<white>${plugin.getLogDisplayName(it::class.java)}</white>: "

            user.sendMessage(
                miniMessage(
                    "<hover:show_text:'${TimeUtil.timestampToDate(it.timestamp)}'><gray>$time</hover> " +
                            "<aqua>${it.offlinePlayer.name}<gray> | ${if (onlyOneType) "" else displayName}${it.message}"
                )
            )

            if (Permission.TELEPORT.isGrantedTo(user) && it.location != null) {
                val teleportCommand =
                    "/$commandName tp ${it.location?.worldName} ${it.location?.x} ${it.location?.y} ${it.location?.z}"

                user.sendMessage(
                    miniMessage("<hover:show_text:'$teleportCommand'><gray>   (${it.location?.worldName}/x${it.location?.x}/y${it.location?.y}/z${it.location?.z})</hover>")
                        .clickEvent(ClickEvent.runCommand(teleportCommand))
                )
            }
        }
        user.sendMessage("&7--------- &bEnd of results &7---------")
    }
}