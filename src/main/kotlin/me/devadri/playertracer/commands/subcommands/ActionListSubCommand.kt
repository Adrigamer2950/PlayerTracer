package me.devadri.playertracer.commands.subcommands

import me.devadri.obsidian.user.User
import me.devadri.playertracer.PlayerTracerPlugin
import me.devadri.playertracer.commands.AbstractPLCommand

class ActionListSubCommand : AbstractPLCommand("actionlist", "Lists all available actions", listOf("al")) {

    override fun getDisplayName(rootCommandName: String): String = "$rootCommandName actionlist"

    override fun execute(
        user: User,
        args: Array<out String>,
        commandName: String
    ) {
        user.sendMessage("&7--------- &bAction List &7--------- ")

        PlayerTracerPlugin.instance.logsProvider.logs.forEach {
            val info = PlayerTracerPlugin.instance.logsProvider.getData(it.clazz)

            user.sendMessage("&b${info.id}&7: ${info.description}")
        }
    }
}