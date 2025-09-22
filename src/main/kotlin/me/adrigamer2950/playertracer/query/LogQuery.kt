package me.adrigamer2950.playertracer.query

import me.adrigamer2950.playertracer.PlayerTracerPlugin
import me.adrigamer2950.playertracer.api.logs.Log
import java.sql.Timestamp
import java.util.UUID
import kotlin.reflect.KClass

class LogQuery(
    val uuids: Array<UUID>,
    val actions: List<KClass<out Log>>,
    val after: Timestamp? = null,
    val afterS: String? = null
) {

    init {
        actions.forEach {
            if (!(PlayerTracerPlugin.instance.logsProvider.isLogRegistered(it))) {
                throw IllegalArgumentException("${it::class.simpleName} is not registered in the logs provider.")
            }
        }
    }

    suspend fun getResults(): List<Log> {
        return PlayerTracerPlugin.instance.database.getLogsAsync(*uuids).filter { log ->
            actions.contains(log::class)
        }.filter { if (after != null) Timestamp(it.timestamp).after(after) else true }
    }
}