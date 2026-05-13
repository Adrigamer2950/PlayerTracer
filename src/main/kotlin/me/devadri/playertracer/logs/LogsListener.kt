package me.devadri.playertracer.logs

import io.papermc.paper.event.player.AsyncChatEvent
import me.devadri.playertracer.PlayerTracerPlugin
import me.devadri.playertracer.api.logs.ChatLog
import me.devadri.playertracer.api.logs.CommandLog
import me.devadri.playertracer.api.logs.JoinServerLog
import me.devadri.playertracer.api.logs.LeaveServerLog
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class LogsListener(private val plugin: PlayerTracerPlugin) : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (plugin.logsProvider.isLogRegistered(JoinServerLog::class.java)) {
            plugin.logsManager.addLog(JoinServerLog(event.player))
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        if (plugin.logsProvider.isLogRegistered(LeaveServerLog::class.java)) {
            plugin.logsManager.addLog(LeaveServerLog(event.player))
        }
    }

    @EventHandler
    fun onPlayerChat(event: AsyncChatEvent) {
        if (plugin.logsProvider.isLogRegistered(ChatLog::class.java)) {
            plugin.logsManager.addLog(ChatLog(event.player, event.message()))
        }
    }

    @EventHandler
    fun onPlayerCommand(event: PlayerCommandPreprocessEvent) {
        if (plugin.logsProvider.isLogRegistered(CommandLog::class.java)) {
            plugin.logsManager.addLog(CommandLog(event.player, event.message))
        }
    }
}