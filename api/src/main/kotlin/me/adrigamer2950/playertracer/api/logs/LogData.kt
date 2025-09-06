package me.adrigamer2950.playertracer.api.logs

class LogData(
    val id: String,
    val description: String,
    val displayName: String = id.replaceFirstChar { it.uppercaseChar() }
)