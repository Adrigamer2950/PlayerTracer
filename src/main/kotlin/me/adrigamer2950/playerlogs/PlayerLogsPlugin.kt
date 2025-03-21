package me.adrigamer2950.playerlogs

import me.adrigamer2950.adriapi.api.APIPlugin
import me.adrigamer2950.playerlogs.util.Asserts

class PlayerLogsPlugin : APIPlugin() {

    override fun onPreLoad() {
        // Enabled while still in development
        isDebug = true

        val preLoadTime = System.currentTimeMillis()

        Asserts.setLogger(this.logger)

        logger.info("&6Loaded in ${System.currentTimeMillis() - preLoadTime}ms")
    }

    override fun onPostLoad() {
        val postLoadTime = System.currentTimeMillis()

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
