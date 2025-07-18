package me.adrigamer2950.playerlogs.commands

import me.adrigamer2950.adriapi.api.AutoRegister
import me.adrigamer2950.adriapi.api.command.AbstractCommand
import me.adrigamer2950.adriapi.api.user.User
import me.adrigamer2950.playerlogs.PlayerLogsPlugin
import me.adrigamer2950.playerlogs.commands.subcommands.ActionListSubCommand
import me.adrigamer2950.playerlogs.commands.subcommands.HelpSubCommand
import me.adrigamer2950.playerlogs.commands.subcommands.PageSubCommand
import me.adrigamer2950.playerlogs.commands.subcommands.SearchSubCommand
import me.adrigamer2950.playerlogs.util.add

// TODO: Add permissions
@Suppress("unused")
@AutoRegister
class MainCommand : AbstractCommand(
    PlayerLogsPlugin.instance,
    "playerlogs",
    "Main command for PlayerLogs plugin",
    listOf("pl", "plogs", "log", "logs")
) {

    init {
        subCommands.add(
            HelpSubCommand(this),
            SearchSubCommand(this),
            PageSubCommand(),
            ActionListSubCommand()
        )
    }

    override fun execute(user: User, args: Array<out String>, commandName: String) {
        executeSubCommands(user, args, commandName)
    }

    override fun tabComplete(user: User, args: Array<out String>, commandName: String): List<String> {
        return suggestSubCommands(user, args, commandName)
    }
}