package me.adrigamer2950.playertracer.api.logs

import me.devadri.obsidian.item.ItemBuilder

class LogData(
    val id: String,
    val description: String,
    val displayName: String = id.replaceFirstChar { it.uppercaseChar() },
    val guiItem: (ItemBuilder, Log) -> ItemBuilder = { item, _ -> item }
)