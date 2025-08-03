package me.adrigamer2950.playertracer.commands.subcommands

import me.adrigamer2950.adriapi.api.user.User
import me.adrigamer2950.playertracer.commands.AbstractPLCommand
import me.adrigamer2950.playertracer.util.Permission
import me.adrigamer2950.playertracer.viewmode.ViewMode
import me.adrigamer2950.playertracer.viewmode.ViewModeManager

class ViewModeSubCommand : AbstractPLCommand("viewmode", "Changes your log viewer type", listOf("vm")) {

    override fun getDisplayName(rootCommandName: String): String = "$rootCommandName viewmode <gui|chat>"

    override fun execute(
        user: User,
        args: Array<out String>,
        commandName: String
    ) {
        if (user.isConsole()) {
            user.sendMessage("&cThis command cannot be used from the console as it cannot use a GUI")
            return
        }

        if (!Permission.SEARCH.isGrantedTo(user)) {
            user.sendMessage("&cYou don't have permission to use this command")
            return
        }

        if (args.isEmpty()) {
            user.sendMessage("&cUsage: ${getDisplayName(commandName)}")
            return
        }

        val viewMode = when (args[0].lowercase()) {
            "gui" -> ViewMode.GUI
            "chat" -> ViewMode.CHAT
            else -> {
                user.sendMessage("&cInvalid view mode. Available modes are 'gui' and 'chat'")
                return
            }
        }

        ViewModeManager.set(user.getPlayerOrNull()!!.uniqueId, viewMode)
    }

    override fun tabComplete(user: User, args: Array<out String>, commandName: String): List<String> {
        return when (args.size) {
            1, 0 -> listOf("gui", "chat")
            else -> emptyList()
        }
    }
}