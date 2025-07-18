package me.adrigamer2950.playerlogs.commands.subcommands

import me.adrigamer2950.adriapi.api.user.User
import me.adrigamer2950.playerlogs.commands.AbstractPLCommand
import me.adrigamer2950.playerlogs.util.Constants.PAGE_SIZE
import me.adrigamer2950.playerlogs.util.TimeUtil
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit

class PageSubCommand : AbstractPLCommand("page", "Shows the specified page of the current search", listOf("p")) {

    override fun getDisplayName(rootCommandName: String): String = "$rootCommandName page &c<page>"

    override fun execute(
        user: User,
        args: Array<out String>,
        commandName: String
    ) {
        val logs = SearchSubCommand.cache[if (user.isConsole()) null else user.getPlayerOrNull()!!.uniqueId] ?: run {
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

        val totalPages = (logs.size / PAGE_SIZE).toInt() + if (logs.size % PAGE_SIZE > 0) 1 else 0

        if (pageNumber < 1 || pageNumber > totalPages) {
            user.sendMessage("&cInvalid page number. Please provide a number between 1 and $totalPages")
            return
        }

        val pagedLogs = logs.subList(0,
            if (pageNumber * PAGE_SIZE > logs.size) logs.size else pageNumber * PAGE_SIZE
        )

        // Display results to the user
        user.sendMessage("&7--------- &bSearch Results &7---------")
        user.sendMessage("&7Page $pageNumber of $totalPages")
        pagedLogs.forEach {
            val mm = MiniMessage.miniMessage()
            user.sendMessage(mm.deserialize("<hover:show_text:'${TimeUtil.timestampToDate(it.timestamp)}'><gray>[${TimeUtil.formatTimeAgo(it.timestamp)}]</hover> " +
                    "<aqua>${Bukkit.getOfflinePlayer(it.playerUUID).name}<gray>: ${it.message}"))
        }
        user.sendMessage("&7--------- &bEnd of results &7---------")
    }
}