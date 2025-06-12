package me.adrigamer2950.playerlogs

import me.adrigamer2950.adriapi.api.APIPlugin
import me.adrigamer2950.playerlogs.logs.*
import me.adrigamer2950.playerlogs.util.Asserts

class PlayerLogsPlugin : APIPlugin() {

    val logsProvider = LogsProvider(this.logger)
    val logsManager = LogsManager(this)

    override fun onPreLoad() {
        // Enabled while still in development
        debug = true

        val preLoadTime = System.currentTimeMillis()

        Asserts.setLogger(this.logger)

        this.logsProvider.registerLog(
            JoinServerLog::class, LeaveServerLog::class, ChatLog::class, CommandLog::class
        )

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
}
