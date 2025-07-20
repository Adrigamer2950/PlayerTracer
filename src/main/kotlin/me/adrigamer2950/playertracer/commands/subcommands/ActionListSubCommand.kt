package me.adrigamer2950.playertracer.commands.subcommands

import me.adrigamer2950.adriapi.api.user.User
import me.adrigamer2950.playertracer.PlayerTracerPlugin
import me.adrigamer2950.playertracer.commands.AbstractPLCommand

class ActionListSubCommand : AbstractPLCommand("actionlist", "Lists all available actions", listOf("al")) {

    override fun getDisplayName(rootCommandName: String): String = "$rootCommandName actionlist"

    override fun execute(
        user: User,
        args: Array<out String>,
        commandName: String
    ) {
        user.sendMessage("&7--------- &bAction List &7--------- ")

        PlayerTracerPlugin.instance.logsProvider.logs.forEach {
            val info = PlayerTracerPlugin.instance.logsProvider.getInfo(it.`class`)

            user.sendMessage("&b${info.id}&7: ${info.description}")
        }
    }
}