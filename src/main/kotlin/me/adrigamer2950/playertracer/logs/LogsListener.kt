package me.adrigamer2950.playertracer.logs

import io.papermc.paper.event.player.AsyncChatEvent
import me.adrigamer2950.playertracer.PlayerTracerPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class LogsListener(private val plugin: PlayerTracerPlugin) : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        plugin.logsManager.addLog(JoinServerLog(event.player))
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        plugin.logsManager.addLog(LeaveServerLog(event.player))
    }

    @EventHandler
    fun onPlayerChat(event: AsyncChatEvent) {
        plugin.logsManager.addLog(ChatLog(event.player, event.message()))
    }

    @EventHandler
    fun onPlayerCommand(event: PlayerCommandPreprocessEvent) {
        plugin.logsManager.addLog(CommandLog(event.player, event.message))
    }
}