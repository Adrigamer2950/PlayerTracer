package me.adrigamer2950.playertracer.api.event

import me.adrigamer2950.playertracer.api.logs.Log
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class LogAddEvent(val log: Log) : Event() {

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList = handlerList
}