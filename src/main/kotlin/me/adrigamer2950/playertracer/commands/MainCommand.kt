package me.adrigamer2950.playertracer.commands

import me.adrigamer2950.adriapi.api.AutoRegister
import me.adrigamer2950.adriapi.api.command.AbstractCommand
import me.adrigamer2950.adriapi.api.user.User
import me.adrigamer2950.playertracer.PlayerTracerPlugin
import me.adrigamer2950.playertracer.commands.subcommands.ActionListSubCommand
import me.adrigamer2950.playertracer.commands.subcommands.HelpSubCommand
import me.adrigamer2950.playertracer.commands.subcommands.PageSubCommand
import me.adrigamer2950.playertracer.commands.subcommands.SearchSubCommand
import me.adrigamer2950.playertracer.commands.subcommands.TeleportSubCommand
import me.adrigamer2950.playertracer.commands.subcommands.ViewModeSubCommand
import me.adrigamer2950.playertracer.util.add

@Suppress("unused")
@AutoRegister
class MainCommand : AbstractCommand(
    PlayerTracerPlugin.instance,
    "playertracer",
    "Main command for PlayerTracer plugin",
    listOf("pt", "ptracer", "trace", "tracer")
) {

    init {
        subCommands.add(
            HelpSubCommand(this),
            SearchSubCommand(this),
            PageSubCommand(),
            ActionListSubCommand(),
            TeleportSubCommand(),
            ViewModeSubCommand()
        )
    }

    override fun execute(user: User, args: Array<out String>, commandName: String) {
        executeSubCommands(user, args, commandName)
    }

    override fun tabComplete(user: User, args: Array<out String>, commandName: String): List<String> {
        return suggestSubCommands(user, args, commandName)
    }
}