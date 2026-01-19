package me.adrigamer2950.playertracer.api.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

private val mm = MiniMessage.miniMessage()

fun miniMessage(input: String): Component = mm.deserialize(input)