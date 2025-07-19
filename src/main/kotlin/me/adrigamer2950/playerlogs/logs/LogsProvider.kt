package me.adrigamer2950.playerlogs.logs

import com.google.gson.Gson
import me.adrigamer2950.adriapi.api.logger.Logger
import me.adrigamer2950.playerlogs.api.logs.Log
import me.adrigamer2950.playerlogs.api.logs.LogInfo
import org.bukkit.plugin.Plugin
import kotlin.reflect.KClass

class LogsProvider(private val logger: Logger) {

    val logs: MutableSet<LogClassInfo> = mutableSetOf()

    /**
     * @throws IllegalArgumentException If the class is not a valid log class or if its id is already registered
     */
    @Throws(IllegalArgumentException::class)
    fun registerLog(plugin: Plugin, vararg classes: KClass<out Log>, jsonParser: Gson = Gson()) {
        classes.forEach {
            if (isLogRegistered(it)) return@forEach

            if (it.qualifiedName == null) {
                throw IllegalArgumentException("Invalid log class. Must not be a local or a class of an anonymous object")
            }

            val duplicated = logs.firstOrNull { log -> getId(log.`class`) == getId(it) }

            if (duplicated != null) {
                throw IllegalArgumentException("A log type with ID '${getId(it)}' is already registered: ${duplicated.`class`.qualifiedName}")
            }

            logs.add(LogClassInfo(it, jsonParser, plugin))

            logger.debug("Registered log ${it.qualifiedName} with id ${getId(it)}")
        }
    }

    fun isLogRegistered(clazz: KClass<out Log>): Boolean = logs.map { it.`class` }.contains(clazz)

    fun encodeToJson(log: Log): String {
        // Get class or throw an exception if not registered
        val pair = logs.firstOrNull { it.`class` == log::class } ?: throw IllegalArgumentException("Log class ${log::class.qualifiedName} is not registered")

        // Encode object to JSON
        return pair.gson.toJson(log)
    }

    fun decodeFromJson(json: String, `class`: KClass<out Log>): Log {
        // Get class or throw an exception if not registered
        val pair = logs.firstOrNull { it.`class` == `class` } ?: throw IllegalArgumentException("Log class ${`class`.qualifiedName} is not registered")

        // Decode object from JSON
        return pair.gson.fromJson(json, `class`.java)
    }

    /**
     * @throws NoSuchFieldException If the class does not have a static field called `INFO`
     * @throws ClassCastException If the field is not of type LogInfo
     */
    @Throws(NoSuchFieldException::class)
    fun getId(`class`: KClass<out Log>): String {
        // Return the ID of the log class
        return getInfo(`class`).id
    }

    /**
     * @throws NoSuchFieldException If the class does not have a static field called `INFO`
     * @throws ClassCastException If the field is not of type LogInfo
     */
    @Throws(NoSuchFieldException::class, ClassCastException::class)
    fun getInfo(`class`: KClass<out Log>): LogInfo {
        // Return the ID of the log class
        return `class`.java.getField("metadata").get(null) as LogInfo
    }

    fun getLogClassById(id: String): KClass<out Log>? = logs.firstOrNull { getId(it.`class`) == id }?.`class`
}

class LogClassInfo(val `class`: KClass<out Log>, val gson: Gson, val plugin: Plugin)