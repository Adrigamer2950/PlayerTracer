package me.adrigamer2950.playerlogs.logs

import com.google.gson.Gson
import me.adrigamer2950.adriapi.api.logger.Logger
import kotlin.reflect.KClass

class LogsProvider(private val logger: Logger) {

    private val logs: MutableSet<LogGsonPair> = mutableSetOf()

    fun registerLog(vararg classes: KClass<out Log>, jsonParser: Gson = Gson()) {
        classes.forEach {
            if (isLogRegistered(it)) return@forEach

            if (it.qualifiedName == null) {
                throw IllegalArgumentException("Invalid log class. Must not be a local or a class of an anonymous object")
            }

            logs.add(LogGsonPair(it, jsonParser))

            logger.debug("Registered log ${it.qualifiedName}")
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
}

class LogGsonPair(val `class`: KClass<out Log>, val gson: Gson)