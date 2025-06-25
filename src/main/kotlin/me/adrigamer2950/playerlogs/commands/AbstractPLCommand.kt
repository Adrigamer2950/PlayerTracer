package me.adrigamer2950.playerlogs.commands

import me.adrigamer2950.adriapi.api.command.AbstractCommand
import me.adrigamer2950.playerlogs.PlayerLogsPlugin

abstract class AbstractPLCommand(
    name: String,
    description: String,
    aliases: List<String> = listOf()
) : AbstractCommand(
    PlayerLogsPlugin.instance,
    name,
    description,
    aliases,
    mutableListOf()
) {

    val commandDescription: String
        get() = super.getDescription()

    abstract fun getDisplayName(rootCommandName: String): String
}