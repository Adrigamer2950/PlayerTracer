package me.adrigamer2950.playertracer.api

import com.google.gson.Gson
import me.adrigamer2950.playertracer.api.logs.Log
import org.bukkit.plugin.Plugin
import java.sql.Timestamp
import java.util.UUID
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

interface PlayerTracer {

    /**
     * Registers a log
     * @throws IllegalArgumentException If the class is not a valid log class or if its id is already registered
     */
    fun registerLog(plugin: Plugin, vararg classes: KClass<out Log>, jsonParser: Gson = Gson())

    /**
     * Registers a log
     * @throws IllegalArgumentException If the class is not a valid log class or if its id is already registered
     */
    fun registerLog(plugin: Plugin, vararg classes: Pair<KClass<out Log>, Gson>)

    /**
     * Adds a log to the database
     */
    fun addLog(log: Log)

    /**
     * Retrieves all logs for the given UUIDs and actions
     * in an asynchronous manner
     *
     * @param uuids The UUIDs of the players to retrieve logs for
     * @param actions The list of log classes to filter by
     * @param after Optional timestamp to filter logs after a certain time
     * @return A CompletableFuture of the list of logs
     */
    fun getLogs(uuids: Array<UUID>, actions: List<KClass<out Log>>, after: Timestamp? = null) : CompletableFuture<List<Log>>
}