package me.adrigamer2950.playertracer.commands.subcommands

import me.adrigamer2950.adriapi.api.command.Command
import me.adrigamer2950.adriapi.api.user.User
import me.adrigamer2950.playertracer.commands.AbstractPLCommand

class HelpSubCommand(private val parent: Command) : AbstractPLCommand("help", "Shows all available commands") {

    override fun execute(user: User, args: Array<out String>, commandName: String) {
        user.sendMessage(
            "&7------------- &bPlayerTracer &7-------------",
            "&c<> &7- &cRequired argument",
            "&e[] &7- &eOptional argument",
        )

        parent.subCommands.filter { it is AbstractPLCommand }.map { it as AbstractPLCommand }.forEach {
            user.sendMessage(
                "&7/${it.getDisplayName(commandName)} &8- &7${it.description}"
            )
        }
    }

    override fun getDisplayName(rootCommandName: String): String = "$rootCommandName help"
}