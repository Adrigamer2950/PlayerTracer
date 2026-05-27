package me.devadri.playertracer.logs

import me.devadri.playertracer.Config
import me.devadri.playertracer.PlayerTracerPlugin
import me.devadri.playertracer.api.logs.ChatLog
import me.devadri.playertracer.api.logs.CommandLog
import me.devadri.playertracer.api.logs.JoinServerLog
import me.devadri.playertracer.api.logs.LeaveServerLog

object BuiltinLogsLoader {

    @JvmStatic
    fun init() {
        val plugin = PlayerTracerPlugin.instance

        if (Config.Logs.JOIN) {
            plugin.registerLog(plugin,
                JoinServerLog::class.java
            )
        }

        if (Config.Logs.LEAVE) {
            plugin.registerLog(plugin,
                LeaveServerLog::class.java
            )
        }

        if (Config.Logs.CHAT) {
            plugin.registerLog(plugin,
                ChatLog::class.java
            )
        }

        if (Config.Logs.COMMAND) {
            plugin.registerLog(plugin,
                CommandLog::class.java
            )
        }
    }
}