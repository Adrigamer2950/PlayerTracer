package me.adrigamer2950.playertracer

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import me.adrigamer2950.adriapi.api.APIPlugin
import me.adrigamer2950.adriapi.lib.libby.Library
import me.adrigamer2950.playertracer.api.PlayerTracer
import me.adrigamer2950.playertracer.api.logs.Log
import me.adrigamer2950.playertracer.database.impl.H2Database
import me.adrigamer2950.playertracer.logs.*
import me.adrigamer2950.playertracer.util.launchCoroutine
import org.bukkit.plugin.Plugin
import java.sql.Timestamp
import java.util.UUID
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

// TODO: config.yml
// TODO: messages.yml
class PlayerTracerPlugin : APIPlugin(), PlayerTracer {

    companion object {
        lateinit var instance: PlayerTracerPlugin
            private set
    }

    val logsProvider = LogsProvider(this.logger)
    val logsManager = LogsManager(this)
    val database = H2Database(this)

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
                    .groupId("org.jetbrains.kotlin")
                    .artifactId("kotlin-reflect")
                    .version(BuildConstants.KOTLIN_VERSION)
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
        }

        database.connect()

        this.logsProvider.registerLog(this, JoinServerLog::class, LeaveServerLog::class, ChatLog::class, CommandLog::class)

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
    ): CompletableFuture<List<Log>> {
        val future = CompletableFuture<List<Log>>()

        launchCoroutine(Dispatchers.Default) {
            try {
                val logs = LogQuery(uuids, actions, after).getResults()

                future.complete(logs)
            } catch (e: Exception) {
                future.completeExceptionally(e)
            }
        }

        return future
    }
}
