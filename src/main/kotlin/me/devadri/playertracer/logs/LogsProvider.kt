package me.devadri.playertracer.logs

import com.google.gson.Gson
import me.devadri.obsidian.logger.Logger
import me.devadri.playertracer.api.event.LogRegisterEvent
import me.devadri.playertracer.api.logs.Log
import me.devadri.playertracer.api.logs.LogMetadata
import me.devadri.playertracer.api.logs.filter.LogQueryFilter
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import kotlin.reflect.full.findAnnotation

class LogsProvider(private val logger: Logger) {

    val logs: MutableSet<LogClassInfo> = mutableSetOf()

    /**
     * @throws IllegalArgumentException If the class is not a valid log class or if its id is already registered
     */
    @Throws(IllegalArgumentException::class)
    fun registerLog(plugin: Plugin, vararg classes: Class<out Log>, jsonParser: Gson = Gson()) {
        classes.forEach {
            if (isLogRegistered(it)) return@forEach

            if (it.kotlin.qualifiedName == null) {
                throw IllegalArgumentException("Invalid log class. Must not be a local or a class of an anonymous object")
            }

            val duplicated = logs.firstOrNull { log -> getId(log.clazz) == getId(it) }

            if (duplicated != null) {
                throw IllegalArgumentException("A log type with ID '${getId(it)}' is already registered: ${duplicated.clazz.kotlin.qualifiedName}")
            }

            logs.add(LogClassInfo(it, jsonParser, plugin))

            Bukkit.getPluginManager().callEvent(LogRegisterEvent(it))

            logger.debug("Registered log ${it.kotlin.qualifiedName} with id ${getId(it)}")
        }
    }

    fun isLogRegistered(clazz: Class<out Log>): Boolean = logs.map { it.clazz }.contains(clazz)

    fun encodeToJson(log: Log): String {
        // Get class or throw an exception if not registered
        val pair = logs.firstOrNull { it.clazz == log::class.java }
            ?: throw IllegalArgumentException("Log class ${log::class.qualifiedName} is not registered")

        // Encode object to JSON
        return pair.gson.toJson(log)
    }

    fun decodeFromJson(json: String, `class`: Class<out Log>): Log {
        // Get class or throw an exception if not registered
        val pair = logs.firstOrNull { it.clazz == `class` }
            ?: throw IllegalArgumentException("Log class ${`class`.kotlin.qualifiedName} is not registered")

        // Decode object from JSON
        return pair.gson.fromJson(json, `class`)
    }

    /**
     * @throws NoSuchFieldException If the class does not have a static field called `metadata`
     * @throws ClassCastException If the field is not of type LogData
     */
    @Throws(NoSuchFieldException::class)
    fun getId(`class`: Class<out Log>): String {
        return getData(`class`).id
    }

    /**
     * @throws IllegalStateException If the class is not annotated with [LogMetadata]
     */
    @Throws(IllegalStateException::class)
    fun getData(`class`: Class<out Log>): LogMetadata =
        `class`.kotlin.findAnnotation<LogMetadata>()
            ?: error("${`class`.simpleName} is not annotated with @LogMetadata")

    fun getLogClassById(id: String): Class<out Log>? = logs.firstOrNull { getId(it.clazz) == id }?.clazz
    
    fun getLogQueryFilterInstance(`class`: Class<out Log>): LogQueryFilter {
        val data = getData(`class`)
        val queryFilterClass = data.queryFilter
        
        return queryFilterClass.java.getDeclaredConstructor().newInstance()
    }
}

class LogClassInfo(val clazz: Class<out Log>, val gson: Gson, val plugin: Plugin)