package me.devadri.playertracer.commands.subcommands

import me.devadri.obsidian.command.Command
import me.devadri.obsidian.user.User
import me.devadri.playertracer.commands.AbstractPLCommand

class HelpSubCommand(private val parent: Command) : AbstractPLCommand("help", "Shows all available commands") {

    override fun execute(user: User, args: Array<out String>, commandName: String) {
        user.sendMessage(
            "&7------------- &bPlayerTracer &7-------------",
            "&c<> &7- &cRequired argument",
            "&e[] &7- &eOptional argument",
        )

        parent.subCommands.filterIsInstance<AbstractPLCommand>().map { it }.forEach {
            user.sendMessage(
                "&7/${it.getDisplayName(commandName)} &8- &7${it.description}"
            )
        }
    }

    override fun getDisplayName(rootCommandName: String): String = "$rootCommandName help"
}