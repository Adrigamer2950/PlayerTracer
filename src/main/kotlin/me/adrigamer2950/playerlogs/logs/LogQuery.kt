package me.adrigamer2950.playerlogs.logs

import me.adrigamer2950.playerlogs.PlayerLogsPlugin
import java.sql.Timestamp
import java.util.*
import kotlin.reflect.KClass

class LogQuery(
    val uuids: Array<UUID>,
    val actions: List<KClass<out Log>>,
    val after: Timestamp? = null
) {

    init {
        actions.forEach {
            if (!(PlayerLogsPlugin.instance.logsProvider.isLogRegistered(it))) {
                throw IllegalArgumentException("${it::class.simpleName} is not registered in the logs provider.")
            }
        }
    }

    fun getResults(): List<Log> {
        return PlayerLogsPlugin.instance.database.getLogs(*uuids).filter { log ->
            actions.contains(log::class)
        }.filter { if (after != null) Timestamp(it.timestamp).after(after) else true }
    }
}