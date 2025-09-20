package me.adrigamer2950.playertracer.commands.subcommands

import me.devadri.obsidian.user.User
import me.adrigamer2950.playertracer.commands.AbstractPLCommand
import me.adrigamer2950.playertracer.util.Constants.PAGE_SIZE
import me.adrigamer2950.playertracer.util.Permission
import me.adrigamer2950.playertracer.util.TimeUtil
import me.adrigamer2950.playertracer.util.miniMessage
import me.devadri.obsidian.asPlayer
import me.devadri.obsidian.isConsole
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Bukkit

class PageSubCommand : AbstractPLCommand("page", "Shows the specified page of the current search", listOf("p")) {

    override fun getDisplayName(rootCommandName: String): String = "$rootCommandName page &c<page>"

    override fun execute(
        user: User,
        args: Array<out String>,
        commandName: String
    ) {
        val logs = SearchSubCommand.cache[if (user.isConsole()) null else user.asPlayer()!!.uniqueId] ?: run {
            user.sendMessage("&cNo search results found. Please run a search first.")
            return
        }

        if (args.isEmpty()) {
            user.sendMessage("&cUsage: /${getDisplayName(commandName)}")
            return
        }

        val pageNumber = args[0].toIntOrNull() ?: run {
            user.sendMessage("&cInvalid page number. Please provide a valid integer.")
            return
        }

        val totalPages = (logs.size / PAGE_SIZE) + if (logs.size % PAGE_SIZE > 0) 1 else 0

        if (pageNumber < 1 || pageNumber > totalPages) {
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
            val data = plugin.logsProvider.getData(it::class)

            user.sendMessage(
                miniMessage(
                    "<hover:show_text:'${TimeUtil.timestampToDate(it.timestamp)}'><gray>$time</hover> " +
                            "<aqua>${Bukkit.getOfflinePlayer(it.playerUUID).name}<gray> | <white>${data.displayName}</white>: ${it.message}"
                )
            )

            if (Permission.TELEPORT.isGrantedTo(user)) {
                val teleportCommand =
                    "/$commandName tp ${it.location.worldName} ${it.location.x} ${it.location.y} ${it.location.z}"

                val space = " ".repeat(time.length)

                user.sendMessage(
                    miniMessage("<hover:show_text:'$teleportCommand'><gray>$space (${it.location.worldName}/x${it.location.x}/y${it.location.y}/z${it.location.z})</hover>")
                        .clickEvent(ClickEvent.runCommand(teleportCommand))
                )
            }
        }
        user.sendMessage("&7--------- &bEnd of results &7---------")
    }
}