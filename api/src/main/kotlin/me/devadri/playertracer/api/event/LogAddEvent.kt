package me.devadri.playertracer.api.event

import me.devadri.playertracer.api.logs.Log
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Triggered when a log is added to the database
 */
class LogAddEvent(val log: Log) : Event(true) {

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList = handlerList
}