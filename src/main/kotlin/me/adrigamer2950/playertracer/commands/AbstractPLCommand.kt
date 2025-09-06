package me.adrigamer2950.playertracer.commands

import me.devadri.obsidian.command.AbstractCommand
import me.adrigamer2950.playertracer.PlayerTracerPlugin

abstract class AbstractPLCommand(
    name: String,
    description: String,
    aliases: List<String> = listOf()
) : AbstractCommand(
    PlayerTracerPlugin.instance,
    name,
    description,
    aliases,
    mutableListOf()
) {

    abstract fun getDisplayName(rootCommandName: String): String
}