package me.devadri.playertracer.commands

import me.devadri.obsidian.AutoRegister
import me.devadri.obsidian.command.AbstractCommand
import me.devadri.obsidian.user.User
import me.devadri.playertracer.PlayerTracerPlugin
import me.devadri.playertracer.commands.subcommands.ActionListSubCommand
import me.devadri.playertracer.commands.subcommands.HelpSubCommand
import me.devadri.playertracer.commands.subcommands.PageSubCommand
import me.devadri.playertracer.commands.subcommands.SearchSubCommand
import me.devadri.playertracer.commands.subcommands.TeleportSubCommand
import me.devadri.playertracer.commands.subcommands.ViewModeSubCommand
import me.devadri.playertracer.util.add

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