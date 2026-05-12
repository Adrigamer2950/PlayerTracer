package me.devadri.playertracer.query

import me.devadri.playertracer.PlayerTracerPlugin
import me.devadri.playertracer.api.logs.Log
import java.sql.Timestamp
import java.util.UUID

class LogQuery(
    val uuids: Array<UUID>,
    val actions: List<Class<out Log>>,
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
        return PlayerTracerPlugin.instance.database.getLogs(*uuids).filter { log ->
            actions.contains(log::class.java)
        }.filter { if (after != null) Timestamp(it.timestamp).after(after) else true }
    }
}