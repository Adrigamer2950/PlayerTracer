package me.devadri.playertracer.api.event

import me.devadri.playertracer.api.logs.Log
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class LogRegisterEvent(val klass: Class<out Log>) : Event() {

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList = handlerList
}