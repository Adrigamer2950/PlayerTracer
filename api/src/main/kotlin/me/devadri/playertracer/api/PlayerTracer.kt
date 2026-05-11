package me.devadri.playertracer.api

import com.google.gson.Gson
import me.devadri.playertracer.api.logs.Log
import me.devadri.playertracer.api.logs.LogMetadata
import org.bukkit.plugin.Plugin
import java.sql.Timestamp
import java.util.UUID
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

interface PlayerTracer {

    /**
     * Registers a log
     * @throws IllegalArgumentException If the class is not a valid log class or if its id is already registered
     * @param plugin The plugin that owns the log
     * @param classes The log classes to register
     * @param jsonParser The JSON parser to use for serializing and deserializing the specified logs
     */
    fun registerLog(plugin: Plugin, vararg classes: KClass<out Log>, jsonParser: Gson = Gson())

    /**
     * Registers a log
     * @throws IllegalArgumentException If the class is not a valid log class or if its id is already registered
     * @param plugin The plugin that owns the log
     * @param classes The log classes to register + their respective JSON parsers
     */
    fun registerLog(plugin: Plugin, vararg classes: Pair<KClass<out Log>, Gson>)

    /**
     * Adds a log to the database, triggering the LogAddEvent (async)
     */
    fun addLog(log: Log)

    /**
     * Retrieves all logs for the given UUIDs and actions
     * in an asynchronous manner
     *
     * @param uuids The UUIDs of the players to retrieve logs for
     * @param actions The list of log classes to filter by
     * @param after Optional timestamp to filter logs after a certain time
     * @return A [CompletableFuture] of the list of logs
     */
    fun getLogs(uuids: Array<UUID>, actions: List<KClass<out Log>>, after: Timestamp? = null) : CompletableFuture<List<Log>>

    /**
     * Retrieves all logs for the given UUIDs and actions using Kotlin's coroutines
     *
     * @param uuids The UUIDs of the players to retrieve logs for
     * @param actions The list of log classes to filter by
     * @param after Optional timestamp to filter logs after a certain time
     * @return A [List] of logs
     */
    suspend fun getLogsAsync(uuids: Array<UUID>, actions: List<KClass<out Log>>, after: Timestamp? = null) : List<Log>

    /**
     * Retrieves the [LogMetadata] for the given log class
     *
     * @param logClass The log class to retrieve metadata from
     * @return The [LogMetadata] for the given log class
     * @throws IllegalStateException If the class is not annotated with [LogMetadata]
     */
    @Throws(NoSuchFieldException::class, ClassCastException::class)
    fun getLogMetadata(logClass: KClass<out Log>): LogMetadata

    /**
     * Retrieves the Display Name for the given log class
     *
     * @param logClass The log class to retrieve the display name from
     * @return The Display Name of the given log class
     * @throws IllegalStateException If the class is not annotated with [LogMetadata]
     */
    fun getLogDisplayName(logClass: KClass<out Log>): String
}