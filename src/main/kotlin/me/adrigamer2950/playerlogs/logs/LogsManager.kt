package me.adrigamer2950.playerlogs.logs

import me.adrigamer2950.playerlogs.PlayerLogsPlugin

class LogsManager(private val plugin: PlayerLogsPlugin) {

    fun addLog(log: Log) {
        if (!plugin.logsProvider.isLogRegistered(log::class))
            throw IllegalArgumentException("Log class isn't registered")

        plugin.database.addLog(log)
    }
}