package me.adrigamer2950.playerlogs.api

import com.google.gson.Gson
import me.adrigamer2950.playerlogs.api.logs.Log
import org.bukkit.plugin.Plugin
import kotlin.reflect.KClass

// TODO: Add documentation
interface PlayerLogs {

    /**
     * @throws IllegalArgumentException If the class is not a valid log class or if its id is already registered
     */
    fun registerLog(plugin: Plugin, vararg classes: KClass<out Log>, jsonParser: Gson = Gson())

    /**
     * @throws IllegalArgumentException If the class is not a valid log class or if its id is already registered
     */
    fun registerLog(plugin: Plugin, vararg classes: Pair<KClass<out Log>, Gson>)

    fun addLog(log: Log)
}