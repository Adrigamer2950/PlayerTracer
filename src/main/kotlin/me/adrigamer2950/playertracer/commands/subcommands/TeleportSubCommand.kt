package me.adrigamer2950.playertracer.commands.subcommands

import me.adrigamer2950.adriapi.api.user.User
import me.adrigamer2950.playertracer.commands.AbstractPLCommand
import me.adrigamer2950.playertracer.util.Permission
import org.bukkit.Bukkit
import org.bukkit.Location

class TeleportSubCommand : AbstractPLCommand("tp", "Teleports you to a specified location") {

    override fun getDisplayName(rootCommandName: String): String = "$rootCommandName tp &c<world> <x> <y> <z>"

    override fun execute(
        user: User,
        args: Array<out String>,
        commandName: String
    ) {
        if (user.isConsole()) {
            user.sendMessage("&cThis command cannot be used from the console.")
            return
        }

        if (!Permission.TELEPORT.isGrantedTo(user)) {
            user.sendMessage("&cYou don't have permission to use this command")
            return
        }

        if (args.isEmpty() || args.size < 4) {
            user.sendMessage("&cUsage: /${getDisplayName(commandName)}")
            return
        }

        val world = Bukkit.getWorld(args[0]) ?: run {
            user.sendMessage("&cWorld &6${args[0]} &cnot found")
            return
        }

        val x = args[1].toDoubleOrNull() ?: run {
            user.sendMessage("&cInvalid X coordinate: ${args[1]}")
            return
        }

        val y = args[2].toDoubleOrNull() ?: run {
            user.sendMessage("&cInvalid Y coordinate: ${args[2]}")
            return
        }

        val z = args[3].toDoubleOrNull() ?: run {
            user.sendMessage("&cInvalid Z coordinate: ${args[3]}")
            return
        }

        user.getPlayerOrNull()!!.teleportAsync(Location(world, x, y, z))
    }
}