package me.adrigamer2950.playertracer.logs

import me.adrigamer2950.playertracer.PlayerTracerPlugin
import me.adrigamer2950.playertracer.api.logs.Log

class LogsManager(private val plugin: PlayerTracerPlugin) {

    fun addLog(log: Log) {
        if (!plugin.logsProvider.isLogRegistered(log::class))
            throw IllegalArgumentException("Log class isn't registered")

        plugin.database.addLog(log)
    }
}