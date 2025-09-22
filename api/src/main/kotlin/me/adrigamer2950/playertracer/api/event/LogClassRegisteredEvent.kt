package me.adrigamer2950.playertracer.api.event

import me.adrigamer2950.playertracer.api.logs.Log
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class LogClassRegisteredEvent(val klass: Class<out Log>) : Event() {

    companion object {
        @JvmStatic
        val handlers = HandlerList()
    }

    override fun getHandlers(): HandlerList = handlers
}