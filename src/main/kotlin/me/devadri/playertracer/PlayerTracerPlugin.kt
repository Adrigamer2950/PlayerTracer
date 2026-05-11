package me.devadri.playertracer

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import me.devadri.playertracer.api.PlayerTracer
import me.devadri.playertracer.api.logs.Log
import me.devadri.playertracer.database.LogsDatabase
import me.devadri.playertracer.database.impl.H2Database
import me.devadri.playertracer.database.impl.SQLiteDatabase
import me.devadri.playertracer.database.impl.remote.MariaDBDatabase
import me.devadri.playertracer.database.impl.remote.MySQLDatabase
import me.devadri.playertracer.database.impl.remote.PostgreSQLDatabase
import me.devadri.playertracer.logs.*
import me.devadri.playertracer.query.LogQuery
import me.devadri.playertracer.util.launchCoroutine
import me.devadri.playertracer.viewmode.ViewModeManager
import me.devadri.obsidian.ObsidianPlugin
import me.devadri.obsidian.lib.libby.Library
import me.devadri.playertracer.api.logs.LogMetadata
import org.bukkit.plugin.Plugin
import java.sql.Timestamp
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

// TODO: messages.yml
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

        val preLoadTime = System.currentTimeMillis()

        try {
            libraryManager.loadLibraries(
                Library.builder()
                    .groupId("com.h2database")
                    .artifactId("h2")
                    .version(BuildConstants.H2_VERSION)
                    .build(),
                Library.builder()
                    .groupId("org.xerial")
                    .artifactId("sqlite-jdbc")
                    .version(BuildConstants.SQLITE_VERSION)
                    .build(),
                Library.builder()
                    .groupId("mysql")
                    .artifactId("mysql-connector-java")
                    .version(BuildConstants.MYSQL_VERSION)
                    .build(),
                Library.builder()
                    .groupId("org.mariadb.jdbc")
                    .artifactId("mariadb-java-client")
                    .version(BuildConstants.MARIADB_VERSION)
                    .build(),
                Library.builder()
                    .groupId("org.postgresql")
                    .artifactId("postgresql")
                    .version(BuildConstants.POSTGRESQL_VERSION)
                    .build(),
                Library.builder()
                    .groupId("org.jetbrains.kotlin")
                    .artifactId("kotlin-reflect")
                    .version(BuildConstants.KOTLIN_VERSION)
                    .build(),
                Library.builder()
                    .groupId("dev.dejvokep")
                    .artifactId("boosted-yaml")
                    .version(BuildConstants.BOOSTED_YAML_VERSION)
                    .build()
            )

            listOf("core", "dao", "jdbc").forEach {
                libraryManager.loadLibraries(
                    Library.builder()
                        .groupId("org.jetbrains.exposed")
                        .artifactId("exposed-$it")
                        .version(BuildConstants.EXPOSED_VERSION)
                        .resolveTransitiveDependencies(true)
                        .build()
                )
            }
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

        this.logsProvider.registerLog(
            this,
            JoinServerLog::class,
            LeaveServerLog::class,
            ChatLog::class,
            CommandLog::class
        )

        ViewModeManager.init()

        logger.info("&6Loaded in ${System.currentTimeMillis() - preLoadTime}ms")
    }

    override fun onPostLoad() {
        val postLoadTime = System.currentTimeMillis()

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

        logger.info("&aEnabled in ${System.currentTimeMillis() - postLoadTime}ms")
    }

    override fun onUnload() {
        ViewModeManager.save()

        logger.info("&cDisabled")
    }

    override fun registerLog(
        plugin: Plugin,
        vararg classes: KClass<out Log>,
        jsonParser: Gson
    ) {
        logsProvider.registerLog(plugin, *classes, jsonParser = jsonParser)
    }

    override fun registerLog(
        plugin: Plugin,
        vararg classes: Pair<KClass<out Log>, Gson>
    ) {
        classes.forEach {
            logsProvider.registerLog(plugin, it.first, jsonParser = it.second)
        }
    }

    override fun addLog(log: Log) {
        logsManager.addLog(log)
    }

    override fun getLogs(
        uuids: Array<UUID>,
        actions: List<KClass<out Log>>,
        after: Timestamp?
    ): CompletableFuture<List<Log>> = getLogsWithFuture(LogQuery(uuids, actions, after))

    internal fun getLogsWithFuture(query: LogQuery): CompletableFuture<List<Log>> {
        val future = CompletableFuture<List<Log>>()

        launchCoroutine(Dispatchers.Default) {
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
        actions: List<KClass<out Log>>,
        after: Timestamp?
    ): List<Log> = getLogs(LogQuery(uuids, actions, after))

    override fun getLogMetadata(logClass: KClass<out Log>): LogMetadata =
        this.logsProvider.getData(logClass)

    override fun getLogDisplayName(logClass: KClass<out Log>): String {
        val metadata = getLogMetadata(logClass)

        return if (metadata.displayName == "")
            metadata.id.replaceFirstChar { it.uppercaseChar() }
        else
            metadata.displayName
    }
}
