package me.devadri.playertracer

import com.google.gson.Gson
import me.devadri.obsidian.ObsidianPlugin
import me.devadri.obsidian.lib.libby.Library
import me.devadri.playertracer.api.PlayerTracer
import me.devadri.playertracer.api.logs.Log
import me.devadri.playertracer.api.logs.LogMetadata
import me.devadri.playertracer.database.LogsDatabase
import me.devadri.playertracer.database.impl.H2Database
import me.devadri.playertracer.database.impl.SQLiteDatabase
import me.devadri.playertracer.database.impl.remote.MariaDBDatabase
import me.devadri.playertracer.database.impl.remote.MySQLDatabase
import me.devadri.playertracer.database.impl.remote.PostgreSQLDatabase
import me.devadri.playertracer.logs.BuiltinLogsLoader
import me.devadri.playertracer.logs.LogsListener
import me.devadri.playertracer.logs.LogsManager
import me.devadri.playertracer.logs.LogsProvider
import me.devadri.playertracer.query.LogQuery
import me.devadri.playertracer.util.launchCoroutine
import me.devadri.playertracer.viewmode.ViewModeManager
import org.bukkit.plugin.Plugin
import java.sql.Timestamp
import java.util.*
import java.util.concurrent.CompletableFuture

class PlayerTracerPlugin : ObsidianPlugin(), PlayerTracer {

    companion object {
        lateinit var instance: PlayerTracerPlugin
            private set
    }

    val logsProvider = LogsProvider(this.logger)
    val logsManager = LogsManager(this)
    lateinit var database: LogsDatabase

    override fun onPreLoad() {
        // Enabled while still in development
        debug = true

        instance = this

        try {
            // List of all runtime dependencies
            listOf(
                "com.h2database:h2:${BuildConstants.H2_VERSION}",
                "org.xerial:sqlite-jdbc:${BuildConstants.SQLITE_VERSION}",
                "com.mysql:mysql-connector-j:${BuildConstants.MYSQL_VERSION}",
                "org.mariadb.jdbc:mariadb-java-client:${BuildConstants.MARIADB_VERSION}",
                "org.postgresql:postgresql:${BuildConstants.POSTGRESQL_VERSION}",
                "org.jetbrains.kotlin:kotlin-reflect:${BuildConstants.KOTLIN_VERSION}",
                "dev.dejvokep:boosted-yaml:${BuildConstants.BOOSTED_YAML_VERSION}",
                "org.jetbrains.exposed:exposed-core:${BuildConstants.EXPOSED_VERSION}:resolveTransitiveDependencies",
                "org.jetbrains.exposed:exposed-dao:${BuildConstants.EXPOSED_VERSION}:resolveTransitiveDependencies",
                "org.jetbrains.exposed:exposed-jdbc:${BuildConstants.EXPOSED_VERSION}:resolveTransitiveDependencies"
            ).map { l ->
                val list: List<String> = l.split(":")

                // Shouldn't happen in any case, but whatever
                if (list.size < 3) {
                    logger.error("Library '$l' is using an incorrect format. Disabling plugin...")
                    server.pluginManager.disablePlugin(this)
                    return
                }

                val resolveTransitiveDependencies = list.size == 4 && list[3] == "resolveTransitiveDependencies"

                Library.builder()
                    .groupId(list[0])
                    .artifactId(list[1])
                    .version(list[2])
                    .resolveTransitiveDependencies(resolveTransitiveDependencies)
                    .build()
            }.forEach { libraryManager.loadLibrary(it) }
        } catch (e: Exception) {
            logger.error("&cError loading libraries. Shutting down...", e)
            server.pluginManager.disablePlugin(this)
            return
        }

        Config.init()

        database = when (Config.Database.driver) {
            Config.Database.Driver.H2 -> H2Database()
            Config.Database.Driver.SQLITE -> SQLiteDatabase()
            Config.Database.Driver.MYSQL -> MySQLDatabase()
            Config.Database.Driver.MARIADB -> MariaDBDatabase()
            Config.Database.Driver.POSTGRESQL -> PostgreSQLDatabase()
        }

        BuiltinLogsLoader.init()

        ViewModeManager.init()
    }

    override fun onPostLoad() {
        registerListener(LogsListener(this))

        listOf(
            "&e-------------------------------------------------------",
            "&eTHIS PLUGIN IS STILL &lUNDER DEVELOPMENT&r&e, SO IT'S",
            "&e&lNOT&r &eRECOMMENDED TO USE IN PRODUCTION SERVERS",
            "&eUNLESS YOU KNOW WHAT ARE YOU DOING. WHATEVER",
            "&eHAPPENS DURING THIS PHASE, IS &lYOUR RESPONSIBILITY&r&e.",
            "&e-------------------------------------------------------"
        ).forEach {
            logger.warn(it)
        }
    }

    override fun onUnload() {
        ViewModeManager.save()
    }

    override fun registerLog(
        plugin: Plugin,
        vararg classes: Class<out Log>,
        jsonParser: Gson
    ) {
        logsProvider.registerLog(plugin, *classes, jsonParser = jsonParser)
    }

    override fun registerLog(
        plugin: Plugin,
        vararg classes: Pair<Class<out Log>, Gson>
    ) {
        classes.forEach {
            logsProvider.registerLog(plugin, it.first, jsonParser = it.second)
        }
    }

    override fun addLog(log: Log) {
        logsManager.addLog(log)
    }

    override fun getLogsFuture(
        uuids: Array<UUID>,
        actions: List<Class<out Log>>,
        after: Timestamp?
    ): CompletableFuture<List<Log>> = getLogsWithFuture(LogQuery(uuids, actions, after))

    internal fun getLogsWithFuture(query: LogQuery): CompletableFuture<List<Log>> {
        val future = CompletableFuture<List<Log>>()

        launchCoroutine(database.dispatcher) {
            try {
                val logs = query.getResults()

                future.complete(logs)
            } catch (e: Exception) {
                future.completeExceptionally(e)
            }
        }

        return future
    }

    internal suspend fun getLogs(query: LogQuery): List<Log> {
        return query.getResults()
    }

    override suspend fun getLogsAsync(
        uuids: Array<UUID>,
        actions: List<Class<out Log>>,
        after: Timestamp?
    ): List<Log> = getLogs(LogQuery(uuids, actions, after))

    override fun getLogMetadata(logClass: Class<out Log>): LogMetadata =
        this.logsProvider.getData(logClass)

    override fun getLogDisplayName(logClass: Class<out Log>): String {
        val metadata = getLogMetadata(logClass)

        return if (metadata.displayName == "")
            metadata.id.replaceFirstChar { it.uppercaseChar() }
        else
            metadata.displayName
    }
}
