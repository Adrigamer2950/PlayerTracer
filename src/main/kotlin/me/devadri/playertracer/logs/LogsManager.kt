package me.devadri.playertracer.logs

import me.devadri.playertracer.PlayerTracerPlugin
import me.devadri.playertracer.api.event.LogAddEvent
import me.devadri.playertracer.api.logs.Log
import org.bukkit.Bukkit

class LogsManager(private val plugin: PlayerTracerPlugin) {

    fun addLog(log: Log) {
        if (!plugin.logsProvider.isLogRegistered(log::class))
            throw IllegalArgumentException("Log class isn't registered")

        plugin.database.addLog(log)

        plugin.scheduler.async().run {
            Bukkit.getPluginManager().callEvent(LogAddEvent(log))
        }
    }
}