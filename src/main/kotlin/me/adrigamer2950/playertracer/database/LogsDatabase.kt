package me.adrigamer2950.playertracer.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.adrigamer2950.adriapi.api.logger.Logger
import me.adrigamer2950.playertracer.PlayerTracerPlugin
import me.adrigamer2950.playertracer.api.logs.Log
import me.adrigamer2950.playertracer.database.tables.LogsTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNotNull
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

// TODO: Add support for SQLite, MySQL, etc..
abstract class LogsDatabase(protected val plugin: PlayerTracerPlugin) {

    private val logger: Logger = plugin.logger

    lateinit var database: Database

    // Must be overridden. The overridden method should assign a value
    // to the `database` field, then call super.connect()
    open fun connect() {
        if (!::database.isInitialized) {
            throw IllegalStateException("This method wasn't overridden properly. " +
                    "The overridden method must assign a value to the 'database' field, " +
                    "then call super.connect()")
        }

        // Force initial connection to the database and check if connection was successful
        try {
            transaction(database) {
                // Create the logs table if it doesn't exist
                SchemaUtils.create(LogsTable)
            }

            logger.info("Connected to the database successfully.")
        } catch (e: Exception) {
            throw RuntimeException("Failed to connect to the database", e)
        }
    }

    fun addLog(log: Log) {
        transaction(database) {
            // Insert data
            LogsTable.insert {
                it[playerUUID] = log.playerUUID.toString()
                it[`class`] = log::class.qualifiedName as String
                it[data] = plugin.logsProvider.encodeToJson(log)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun getLogs(vararg uuids: UUID): List<Log> {
        return transaction(database) {
            val logs = mutableListOf<Log>()

            // Loop through all logs for the given player UUID and decode them
            LogsTable.select(LogsTable.id, LogsTable.`class`, LogsTable.data)
                .where(
                    LogsTable.data.isNotNull() and LogsTable.`class`.isNotNull()
                            and (LogsTable.playerUUID inList uuids.map { it.toString() }))
                .mapNotNull {
                    val `class` = Class.forName(it[LogsTable.`class`]).kotlin

                    if (!`class`.isSubclassOf(Log::class)) {
                        throw IllegalArgumentException("Class ${it[LogsTable.`class`]} is not a subclass of Log")
                    }

                    // Decode the log that is assumed to be registered in the logs provider
                    // Otherwise, an error is thrown
                    logs.add(plugin.logsProvider.decodeFromJson(it[LogsTable.data], Class.forName(it[LogsTable.`class`]).kotlin as KClass<out Log>))
                }

            return@transaction logs
        }
    }

    suspend fun getLogsAsync(vararg uuids: UUID): List<Log> {
        return withContext(Dispatchers.IO) {
            getLogs(*uuids)
        }
    }
}