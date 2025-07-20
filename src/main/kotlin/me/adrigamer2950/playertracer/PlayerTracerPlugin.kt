package me.adrigamer2950.playertracer

import com.google.gson.Gson
import me.adrigamer2950.adriapi.api.APIPlugin
import me.adrigamer2950.playertracer.api.PlayerTracer
import me.adrigamer2950.playertracer.api.logs.Log
import me.adrigamer2950.playertracer.database.impl.H2Database
import me.adrigamer2950.playertracer.logs.*
import org.bukkit.plugin.Plugin
import kotlin.reflect.KClass

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

        this.logsProvider.registerLog(this, JoinServerLog::class, LeaveServerLog::class, ChatLog::class, CommandLog::class)

        database.connect()

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
}
