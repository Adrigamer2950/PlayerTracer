package me.devadri.playertracer.database

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import me.devadri.obsidian.logger.Logger
import me.devadri.obsidian.util.ClassUtil
import me.devadri.playertracer.Config
import me.devadri.playertracer.PlayerTracerPlugin
import me.devadri.playertracer.api.logs.Log
import me.devadri.playertracer.database.tables.LogsTable
import me.devadri.playertracer.util.launchCoroutine
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.isNotNull
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*
import java.util.concurrent.Executors
import kotlin.reflect.full.isSubclassOf

abstract class LogsDatabase {

    protected val plugin: PlayerTracerPlugin = PlayerTracerPlugin.instance

    private val logger: Logger = plugin.logger

    lateinit var database: Database

    private val dispatcher = Executors.newFixedThreadPool(Config.Database.threadLimit).asCoroutineDispatcher()

    init {
        // Initialize database details and make initial connection
        initializeDatabase()
        connect()
    }

    abstract fun initializeDatabase()

    fun connect() {
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
        launchCoroutine(dispatcher) {
            transaction(database) {
                LogsTable.insert {
                    it[playerUUID] = log.playerUUID.toString()
                    it[`class`] = log::class.qualifiedName as String
                    it[data] = plugin.logsProvider.encodeToJson(log)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun getLogs(vararg uuids: UUID): List<Log> {
        return withContext(dispatcher) {
            transaction(database) {
                val logs = mutableListOf<Log>()

                // Loop through all logs for the given player UUID and decode them
                LogsTable.select(LogsTable.id, LogsTable.`class`, LogsTable.data)
                    .where(
                        LogsTable.data.isNotNull() and LogsTable.`class`.isNotNull()
                                and (LogsTable.playerUUID inList uuids.map { it.toString() })
                    )
                    .forEach {
                        val `class` = ClassUtil.searchForClass(it[LogsTable.`class`]) as? Class<out Log> ?: return@forEach

                        if (!`class`.kotlin.isSubclassOf(Log::class)) {
                            throw IllegalArgumentException("Class ${it[LogsTable.`class`]} is not a subclass of Log")
                        }

                        // Decode the log that is assumed to be registered in the logs provider
                        // Otherwise, an error is thrown
                        logs.add(
                            plugin.logsProvider.decodeFromJson(
                                it[LogsTable.data],
                                `class`
                            ) ?: return@forEach
                        )
                    }

                logs.reversed()
            }
        }
    }
}