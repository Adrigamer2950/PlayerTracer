package me.devadri.playertracer.commands

import me.devadri.obsidian.command.AbstractCommand
import me.devadri.playertracer.PlayerTracerPlugin

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

    override val plugin: PlayerTracerPlugin = super.plugin as PlayerTracerPlugin
}